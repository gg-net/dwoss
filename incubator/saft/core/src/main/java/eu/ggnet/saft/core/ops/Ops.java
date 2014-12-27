/*
 * Copyright (C) 2014 GG-Net GmbH
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
package eu.ggnet.saft.core.ops;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.ggnet.saft.api.ui.*;
import eu.ggnet.saft.core.all.*;

/**
 * Operation Central. The point there Actions and Factories are registered.
 * <p>
 * @author oliver.guenther
 */
public class Ops {

    private final static Logger L = LoggerFactory.getLogger(Ops.class);

    private final static Map<Class, List<DescriptiveConsumer>> REGISTERED_ACTIONS = new HashMap<>();

    private final static Map<Class, DescriptiveConsumer> REGISTERED_DEFAULT_ACTIONS = new HashMap<>();

    private final static Map<Class, List<DescriptiveConsumerFactory>> REGISTERED_ACTION_FACTORIES = new HashMap<>();

    /**
     * Register a new Action Factory.
     * <p>
     * @param <T>     the type is used as key.
     * @param factory the factory to register.
     */
    public static <T> void register(DescriptiveConsumerFactory<T> factory) {
        L.info("Trying to register {}", factory);
        for (Type genericInterface : factory.getClass().getGenericInterfaces()) {
            if ( !genericInterface.toString().contains(DescriptiveConsumerFactory.class.getName()) ) continue;
            // This, we know !
            ParameterizedType pt = (ParameterizedType)genericInterface;
            Class clazz = (Class)pt.getActualTypeArguments()[0];
            L.info("Registering key {} with {}", clazz, factory);
            if ( !REGISTERED_ACTION_FACTORIES.containsKey(clazz) ) REGISTERED_ACTION_FACTORIES.put(clazz, new ArrayList<>());
            REGISTERED_ACTION_FACTORIES.get(clazz).add(factory);
        }
    }

    /**
     * Register a Consumer as global action, possibly annotated for title and more.
     * <p>
     * @param <T>      the type as key, what this action is for.
     * @param consumer the consumer as action.
     */
    public static <T> void register(Consumer<T> consumer) {
        L.info("Trying to register {}", consumer);
        for (Type genericInterface : consumer.getClass().getGenericInterfaces()) {
            if ( !genericInterface.toString().contains(Consumer.class.getName()) ) continue;
            // This, we know !
            ParameterizedType pt = (ParameterizedType)genericInterface;
            Class clazz = (Class)pt.getActualTypeArguments()[0];
            L.info("Registering key {} with {}", clazz, consumer);
            DescriptiveConsumer descriptiveConsumer = new DescriptiveConsumer(consumer);
            if ( consumer.getClass().getAnnotation(DefaultAction.class) != null ) REGISTERED_DEFAULT_ACTIONS.put(clazz, descriptiveConsumer);
            if ( !REGISTERED_ACTIONS.containsKey(clazz) ) REGISTERED_ACTIONS.put(clazz, new ArrayList<>());
            REGISTERED_ACTIONS.get(clazz).add(descriptiveConsumer);
        }
    }

    /**
     * Returns the default DependendAction wrapped in a runner.
     * <p>
     * @param <T>
     * @param t
     * @return
     */
    public static <T> Optional<DescriptiveConsumerRunner<T>> defaultOf(T t) {
        if ( t == null || !REGISTERED_DEFAULT_ACTIONS.containsKey(t.getClass()) ) return Optional.empty();
        return Optional.of(new DescriptiveConsumerRunner<>(REGISTERED_DEFAULT_ACTIONS.get(t.getClass()), t));
    }

    /**
     * Returns all registered dependent actions for this instance as runners, or an empty list never null.
     * <p>
     * @param <T>
     * @param t        this instance as dependent reference
     * @param enhancer an optional enhancer
     * @return all registered dependent actions for this instance as runners, or an empty list never null.
     */
    public static <T> List<DescriptiveConsumerRunner<?>> staticOf(T t, SelectionEnhancer<T> enhancer) {
        Stream<DescriptiveConsumerRunner<T>> map = safeNull(REGISTERED_ACTIONS.get(t.getClass())).stream().map(d -> new DescriptiveConsumerRunner<T>(d, t));
        List<DescriptiveConsumerRunner<?>> result = map.collect(Collectors.toList());
        if ( enhancer == null ) return result;
        for (Object other : enhancer.enhance(t)) {
            for (DescriptiveConsumer otherAction : safeNull(REGISTERED_ACTIONS.get(other.getClass()))) {
                result.add(new DescriptiveConsumerRunner<>(otherAction, other));
            }
        }
        return result;
    }

    public static <T> List<DescriptiveConsumerRunner<?>> dynamicOf(T t, SelectionEnhancer<T> enhancer) {
        List<DescriptiveConsumerRunner<?>> result = new ArrayList<>();
        for (DescriptiveConsumerFactory<T> factory : safeNull(REGISTERED_ACTION_FACTORIES.get(t.getClass()))) {
            for (DescriptiveConsumer<T> action : factory.of(t)) {
                result.add(new DescriptiveConsumerRunner<>(action, t));
            }
        }
        if ( enhancer == null ) return result;
        for (Object other : enhancer.enhance(t)) {
            for (DescriptiveConsumerFactory<Object> factory : safeNull(REGISTERED_ACTION_FACTORIES.get(other.getClass()))) {
                for (DescriptiveConsumer<Object> action : factory.of(other)) {
                    result.add(new DescriptiveConsumerRunner<>(action, t));
                }
            }
        }
        return result;
    }

    private static <T> List<T> safeNull(List<T> in) {
        if ( in != null ) return in;
        return new ArrayList<>();
    }
}
