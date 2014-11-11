/* 
 * Copyright (C) 2014 GG-Net GmbH - Oliver Günther
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package eu.ggnet.dwoss.util;

import java.text.MessageFormat;

import javax.swing.JComponent;

import org.jdesktop.beansbinding.AutoBinding;
import org.jdesktop.beansbinding.Converter;
import org.metawidget.inspector.annotation.MetawidgetAnnotationInspector;
import org.metawidget.inspector.beanvalidation.BeanValidationInspector;
import org.metawidget.inspector.composite.CompositeInspector;
import org.metawidget.inspector.composite.CompositeInspectorConfig;
import org.metawidget.inspector.impl.BaseObjectInspectorConfig;
import org.metawidget.inspector.impl.propertystyle.javabean.JavaBeanPropertyStyleConfig;
import org.metawidget.inspector.impl.propertystyle.javassist.JavassistPropertyStyle;
import org.metawidget.inspector.jpa.JpaInspector;
import org.metawidget.inspector.propertytype.PropertyTypeInspector;
import org.metawidget.swing.SwingMetawidget;
import org.metawidget.swing.layout.*;
import org.metawidget.swing.widgetbuilder.*;
import org.metawidget.swing.widgetbuilder.swingx.SwingXWidgetBuilder;
import org.metawidget.swing.widgetprocessor.binding.beansbinding.BeansBindingProcessor;
import org.metawidget.swing.widgetprocessor.binding.beansbinding.BeansBindingProcessorConfig;
import org.metawidget.widgetbuilder.composite.CompositeWidgetBuilder;
import org.metawidget.widgetbuilder.composite.CompositeWidgetBuilderConfig;

import javassist.ClassClassPath;
import javassist.ClassPool;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Builder;

/**
 * Default config for Metawidget.
 * Default for SwingMetawidget is:
 * <ul>
 * <li>private field inspection</li>
 * <li>PropertyTypeInspector</li>
 * <li>JpaInspector</li>
 * <li>BeanValidationInspector</li>
 * <li>MetawidgetAnnotationInspector</li>
 * <li>SwingXWidgetBuilder</li>
 * <li>SwingWidgetBuilder</li>
 * <li>BeansBindingProcessor</li>
 * </ul>
 * <p/>
 * @author oliver.guenther
 */
public class MetawidgetConfig {

    @RequiredArgsConstructor
    public static class EnumConverter<T extends Enum<T>> extends Converter<T, String> {

        private final Class<T> enumClass;

        @Override
        public String convertForward(T anEnum) {

            // The enum will have been converted to its '.name' by PropertyTypeInspector when
            // it creates lookup values and labels. This means we must also convert the
            // enum to its '.name' during binding.
            //
            // The alternative to this is to have the Metawidgets deal with enums directly, but
            // that is less desirable because it ties the Metawidgets to a Java 5 platform
            return anEnum.name();
        }

        @Override
        public T convertReverse(String name) {
            return Enum.valueOf(enumClass, name);
        }
    }

    /**
     * Enhances the default ClassPool of Javassist to work with a JavaFX Application starter. The ClassPool of javassist
     * is used to load the Classes in javaassist. This is used in Metawidget. This classpool will not find any classes
     * if called from a java fx application. So we need to give in some magic to let it work. The alogrithm works simply
     *
     * @param classes classes as reference for the classpath
     */
    // TODO: Verify, if this is still needed in Java 8
    public static void enhancedMetawidget(Class<?>... classes) {
        ClassPool cp = ClassPool.getDefault();
        for (Class<?> clazz : classes) {
            cp.appendClassPath(new ClassClassPath(clazz));
        }
    }

    /**
     * Returns a new SwingMetawidget with GG-Net defaults and optional enum clazzes beeing in use.
     * <p/>
     * @param clazzes optional enum classes to be converted.
     * @return a new SwingMetawidget with GG-Net defaults and optional enum clazzes beeing in use.
     */
    public static SwingMetawidget newSwingMetaWidget(Class<? extends Enum>... clazzes) {
        return newSwingMetaWidget(false, 1, clazzes);
    }

    /**
     * Returns a new SwingMetawidget with GG-Net defaults, in a gridbagLayout, and optional enum clazzes beeing in use.
     * <p/>
     * @param clazzes        optional enum classes to be converted.
     * @param numberOfColums the number of columns to use. Bigger than 1 activates GridBagLayoutConfig.
     * @param useTabbes      if true, use the TabbedLayout for Sections.
     * @return a new SwingMetawidget with GG-Net defaults and optional enum clazzes beeing in use.
     */
    public static SwingMetawidget newSwingMetaWidget(boolean useTabbes, int numberOfColums, Class<? extends Enum>... clazzes) {
        return newSwingMetaWidget(useTabbes, false, numberOfColums, null, null, clazzes);
    }

    @Builder
    private static SwingMetawidget newSwingMetaWidget(boolean useTabbes, boolean readOnly, int numberOfColums, AutoBinding.UpdateStrategy updateStrategy, Object inspect, Class<? extends Enum>... clazzes) {
        SwingMetawidget metawidget = new SwingMetawidget();
        BaseObjectInspectorConfig inspectorConfig = new BaseObjectInspectorConfig()
                .setPropertyStyle(new JavassistPropertyStyle(new JavaBeanPropertyStyleConfig().setPrivateFieldConvention(new MessageFormat("{0}"))));
        metawidget.setInspector(new CompositeInspector(new CompositeInspectorConfig().setInspectors(
                new PropertyTypeInspector(inspectorConfig),
                new JpaInspector(),
                new BeanValidationInspector(inspectorConfig),
                new MetawidgetAnnotationInspector(inspectorConfig))));

        if ( numberOfColums > 0 ) {
            if ( useTabbes ) {
                metawidget.setMetawidgetLayout(new TabbedPaneLayoutDecorator(new TabbedPaneLayoutDecoratorConfig().setLayout(
                        new GridBagLayout(new GridBagLayoutConfig().setNumberOfColumns(numberOfColums)))));
            } else {
                metawidget.setMetawidgetLayout(new SeparatorLayoutDecorator(new SeparatorLayoutDecoratorConfig().setLayout(
                        new GridBagLayout(new GridBagLayoutConfig().setNumberOfColumns(numberOfColums)))));
            }
        }

        CompositeWidgetBuilderConfig<JComponent, SwingMetawidget> builderConfig = new CompositeWidgetBuilderConfig<>();
        builderConfig.setWidgetBuilders(new OverriddenWidgetBuilder(), new ReadOnlyWidgetBuilder(), new SwingXWidgetBuilder(), new SwingWidgetBuilder());
        metawidget.setWidgetBuilder(new CompositeWidgetBuilder<>(builderConfig));

        BeansBindingProcessorConfig beansBindingProcessorConfig = new BeansBindingProcessorConfig();
        if ( updateStrategy != null ) beansBindingProcessorConfig.setUpdateStrategy(updateStrategy);
        if ( clazzes != null ) {
            for (Class<? extends Enum> clazz : clazzes) {
                beansBindingProcessorConfig.setConverter(clazz, String.class, new EnumConverter(clazz));
            }
        }
        metawidget.addWidgetProcessor(new BeansBindingProcessor(beansBindingProcessorConfig));
        metawidget.setReadOnly(readOnly);
        if ( inspect != null ) metawidget.setToInspect(inspect);
        return metawidget;
    }
}
