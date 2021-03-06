/*
 * Copyright 2016 code_gg_boy, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.xm.core.utils.rx;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.preference.PreferenceManager;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Collections;
import java.util.Set;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Action0;
import rx.subscriptions.Subscriptions;

import static android.os.Build.VERSION_CODES.HONEYCOMB;

/**
 * A factory for reactive {@link Preference} objects.
 */
public final class xmPref {
    private static final Float DEFAULT_FLOAT = Float.valueOf(0);
    private static final Integer DEFAULT_INTEGER = Integer.valueOf(0);
    private static final Boolean DEFAULT_BOOLEAN = Boolean.valueOf(false);
    private static final Long DEFAULT_LONG = Long.valueOf(0);

    /**
     * Create an instance of {@link xmPref} for {@code preferences}.
     */
    @CheckResult
    @NonNull
    public static xmPref init(Context context) {
        return new xmPref(PreferenceManager.getDefaultSharedPreferences(context));
    }

    private final SharedPreferences preferences;
    private final Observable<String> keyChanges;

    private xmPref(final SharedPreferences preferences) {
        this.preferences = preferences;
        this.keyChanges = Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(final Subscriber<? super String> subscriber) {
                final OnSharedPreferenceChangeListener listener = new OnSharedPreferenceChangeListener() {
                    @Override
                    public void onSharedPreferenceChanged(SharedPreferences preferences, String key) {
                        subscriber.onNext(key);
                    }
                };

                preferences.registerOnSharedPreferenceChangeListener(listener);

                subscriber.add(Subscriptions.create(new Action0() {
                    @Override
                    public void call() {
                        preferences.unregisterOnSharedPreferenceChangeListener(listener);
                    }
                }));
            }
        }).share();
    }

    /**
     * Create a boolean preference for {@code key}. Default is {@code false}.
     */
    @CheckResult
    @NonNull
    public Preference<Boolean> getBoolean(@NonNull String key) {
        return getBoolean(key, DEFAULT_BOOLEAN);
    }

    /**
     * Create a boolean preference for {@code key} with a default of {@code defaultValue}.
     */
    @CheckResult
    @NonNull
    public Preference<Boolean> getBoolean(@NonNull String key, @Nullable Boolean defaultValue) {
        Preconditions.checkNotNull(key, "key == null");
        return new Preference<>(preferences, key, defaultValue, BooleanAdapter.INSTANCE, keyChanges);
    }

    /**
     * Create an enum preference for {@code key}. Default is {@code null}.
     */
    @CheckResult
    @NonNull
    public <T extends Enum<T>> Preference<T> getEnum(@NonNull String key,
                                                     @NonNull Class<T> enumClass) {
        return getEnum(key, null, enumClass);
    }

    /**
     * Create an enum preference for {@code key} with a default of {@code defaultValue}.
     */
    @CheckResult
    @NonNull
    public <T extends Enum<T>> Preference<T> getEnum(@NonNull String key, @Nullable T defaultValue,
                                                     @NonNull Class<T> enumClass) {
        Preconditions.checkNotNull(key, "key == null");
        Preconditions.checkNotNull(enumClass, "enumClass == null");
        Preference.Adapter<T> adapter = new EnumAdapter<>(enumClass);
        return new Preference<>(preferences, key, defaultValue, adapter, keyChanges);
    }

    /**
     * Create a float preference for {@code key}. Default is {@code 0}.
     */
    @CheckResult
    @NonNull
    public Preference<Float> getFloat(@NonNull String key) {
        return getFloat(key, DEFAULT_FLOAT);
    }

    /**
     * Create a float preference for {@code key} with a default of {@code defaultValue}.
     */
    @CheckResult
    @NonNull
    public Preference<Float> getFloat(@NonNull String key, @Nullable Float defaultValue) {
        Preconditions.checkNotNull(key, "key == null");
        return new Preference<>(preferences, key, defaultValue, FloatAdapter.INSTANCE, keyChanges);
    }

    /**
     * Create an integer preference for {@code key}. Default is {@code 0}.
     */
    @CheckResult
    @NonNull
    public Preference<Integer> getInteger(@NonNull String key) {
        //noinspection UnnecessaryBoxing
        return getInteger(key, DEFAULT_INTEGER);
    }

    /**
     * Create an integer preference for {@code key} with a default of {@code defaultValue}.
     */
    @CheckResult
    @NonNull
    public Preference<Integer> getInteger(@NonNull String key, @Nullable Integer defaultValue) {
        Preconditions.checkNotNull(key, "key == null");
        return new Preference<>(preferences, key, defaultValue, IntegerAdapter.INSTANCE, keyChanges);
    }

    /**
     * Create a long preference for {@code key}. Default is {@code 0}.
     */
    @CheckResult
    @NonNull
    public Preference<Long> getLong(@NonNull String key) {
        //noinspection UnnecessaryBoxing
        return getLong(key, DEFAULT_LONG);
    }

    /**
     * Create a long preference for {@code key} with a default of {@code defaultValue}.
     */
    @CheckResult
    @NonNull
    public Preference<Long> getLong(@NonNull String key, @Nullable Long defaultValue) {
        Preconditions.checkNotNull(key, "key == null");
        return new Preference<>(preferences, key, defaultValue, LongAdapter.INSTANCE, keyChanges);
    }

    /**
     * Create a preference of type {@code T} for {@code key}. Default is {@code null}.
     */
    @CheckResult
    @NonNull
    public <T> Preference<T> getObject(@NonNull String key, @NonNull Preference.Adapter<T> adapter) {
        return getObject(key, null, adapter);
    }

    /**
     * Create a preference for type {@code T} for {@code key} with a default of {@code defaultValue}.
     */
    @CheckResult
    @NonNull
    public <T> Preference<T> getObject(@NonNull String key, @Nullable T defaultValue,
                                       @NonNull Preference.Adapter<T> adapter) {
        Preconditions.checkNotNull(key, "key == null");
        Preconditions.checkNotNull(adapter, "adapter == null");
        return new Preference<>(preferences, key, defaultValue, adapter, keyChanges);
    }

    /**
     * Create a string preference for {@code key}. Default is {@code null}.
     */
    @CheckResult
    @NonNull
    public Preference<String> getString(@NonNull String key) {
        return getString(key, null);
    }

    /**
     * Create a string preference for {@code key} with a default of {@code defaultValue}.
     */
    @CheckResult
    @NonNull
    public Preference<String> getString(@NonNull String key, @Nullable String defaultValue) {
        Preconditions.checkNotNull(key, "key == null");
        return new Preference<>(preferences, key, defaultValue, StringAdapter.INSTANCE, keyChanges);
    }

    /**
     * Create a string set preference for {@code key}. Default is an empty set.
     */
    @TargetApi(HONEYCOMB)
    @CheckResult
    @NonNull
    public Preference<Set<String>> getStringSet(@NonNull String key) {
        return getStringSet(key, Collections.<String>emptySet());
    }

    /**
     * Create a string set preference for {@code key} with a default of {@code defaultValue}.
     */
    @TargetApi(HONEYCOMB)
    @CheckResult
    @NonNull
    public Preference<Set<String>> getStringSet(@NonNull String key,
                                                @NonNull Set<String> defaultValue) {
        Preconditions.checkNotNull(key, "key == null");
        return new Preference<>(preferences, key, defaultValue, StringSetAdapter.INSTANCE, keyChanges);
    }
}
