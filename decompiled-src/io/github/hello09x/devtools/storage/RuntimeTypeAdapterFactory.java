package io.github.hello09x.devtools.storage;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

public final class RuntimeTypeAdapterFactory<T> implements TypeAdapterFactory {

    private final Class<?> baseType;
    private final String typeFieldName;
    private final Map<String, Class<?>> labelToSubtype = new LinkedHashMap();
    private final Map<Class<?>, String> subtypeToLabel = new LinkedHashMap();
    private final boolean maintainType;
    private boolean recognizeSubtypes;

    private RuntimeTypeAdapterFactory(Class<?> baseType, String typeFieldName, boolean maintainType) {
        super();
        if (typeFieldName != null && baseType != null) {
            this.baseType = baseType;
            this.typeFieldName = typeFieldName;
            this.maintainType = maintainType;
        } else {
            throw new NullPointerException();
        }
    }

    public static <T> RuntimeTypeAdapterFactory<T> of(Class<T> baseType, String typeFieldName, boolean maintainType) {
        return new RuntimeTypeAdapterFactory<T>(baseType, typeFieldName, maintainType);
    }

    public static <T> RuntimeTypeAdapterFactory<T> of(Class<T> baseType, String typeFieldName) {
        return new RuntimeTypeAdapterFactory<T>(baseType, typeFieldName, false);
    }

    public static <T> RuntimeTypeAdapterFactory<T> of(Class<T> baseType) {
        return new RuntimeTypeAdapterFactory<T>(baseType, "type", false);
    }

    @CanIgnoreReturnValue
    public RuntimeTypeAdapterFactory<T> recognizeSubtypes() {
        this.recognizeSubtypes = true;
        return this;
    }

    @CanIgnoreReturnValue
    public RuntimeTypeAdapterFactory<T> registerSubtype(Class<? extends T> type, String label) {
        if (type != null && label != null) {
            if (!this.subtypeToLabel.containsKey(type) && !this.labelToSubtype.containsKey(label)) {
                this.labelToSubtype.put(label, type);
                this.subtypeToLabel.put(type, label);
                return this;
            } else {
                throw new IllegalArgumentException("types and labels must be unique");
            }
        } else {
            throw new NullPointerException();
        }
    }

    @CanIgnoreReturnValue
    public RuntimeTypeAdapterFactory<T> registerSubtype(Class<? extends T> type) {
        return this.registerSubtype(type, type.getSimpleName());
    }

    public <R> TypeAdapter<R> create(Gson gson, TypeToken<R> type) {
        if (type == null) {
            return null;
        } else {
            Class<?> rawType = type.getRawType();
            boolean handle = this.recognizeSubtypes ? this.baseType.isAssignableFrom(rawType) : this.baseType.equals(rawType);

            if (!handle) {
                return null;
            } else {
                final TypeAdapter<JsonElement> jsonElementAdapter = gson.getAdapter(JsonElement.class);
                final Map<String, TypeAdapter<?>> labelToDelegate = new LinkedHashMap();
                final Map<Class<?>, TypeAdapter<?>> subtypeToDelegate = new LinkedHashMap();

                for (Map.Entry<String, Class<?>> entry : this.labelToSubtype.entrySet()) {
                    TypeAdapter<?> delegate = gson.getDelegateAdapter(this, TypeToken.get((Class) entry.getValue()));

                    labelToDelegate.put((String) entry.getKey(), delegate);
                    subtypeToDelegate.put((Class) entry.getValue(), delegate);
                }

                return (new TypeAdapter<R>() {
                    public R read(JsonReader in) throws IOException {
                        JsonElement jsonElement = (JsonElement) jsonElementAdapter.read(in);
                        JsonElement labelJsonElement;

                        if (RuntimeTypeAdapterFactory.this.maintainType) {
                            labelJsonElement = jsonElement.getAsJsonObject().get(RuntimeTypeAdapterFactory.this.typeFieldName);
                        } else {
                            labelJsonElement = jsonElement.getAsJsonObject().remove(RuntimeTypeAdapterFactory.this.typeFieldName);
                        }

                        if (labelJsonElement == null) {
                            throw new JsonParseException("cannot deserialize " + RuntimeTypeAdapterFactory.this.baseType + " because it does not define a field named " + RuntimeTypeAdapterFactory.this.typeFieldName);
                        } else {
                            String label = labelJsonElement.getAsString();
                            TypeAdapter<R> delegate = (TypeAdapter) labelToDelegate.get(label);

                            if (delegate == null) {
                                throw new JsonParseException("cannot deserialize " + RuntimeTypeAdapterFactory.this.baseType + " subtype named " + label + "; did you forget to register a subtype?");
                            } else {
                                return (R) delegate.fromJsonTree(jsonElement);
                            }
                        }
                    }

                    public void write(JsonWriter out, R value) throws IOException {
                        Class<?> srcType = value.getClass();
                        String label = (String) RuntimeTypeAdapterFactory.this.subtypeToLabel.get(srcType);
                        TypeAdapter<R> delegate = (TypeAdapter) subtypeToDelegate.get(srcType);

                        if (delegate == null) {
                            throw new JsonParseException("cannot serialize " + srcType.getName() + "; did you forget to register a subtype?");
                        } else {
                            JsonObject jsonObject = delegate.toJsonTree(value).getAsJsonObject();

                            if (RuntimeTypeAdapterFactory.this.maintainType) {
                                jsonElementAdapter.write(out, jsonObject);
                            } else {
                                JsonObject clone = new JsonObject();

                                if (jsonObject.has(RuntimeTypeAdapterFactory.this.typeFieldName)) {
                                    String s = srcType.getName();

                                    throw new JsonParseException("cannot serialize " + s + " because it already defines a field named " + RuntimeTypeAdapterFactory.this.typeFieldName);
                                } else {
                                    clone.add(RuntimeTypeAdapterFactory.this.typeFieldName, new JsonPrimitive(label));

                                    for (Map.Entry<String, JsonElement> e : jsonObject.entrySet()) {
                                        clone.add((String) e.getKey(), (JsonElement) e.getValue());
                                    }

                                    jsonElementAdapter.write(out, clone);
                                }
                            }
                        }
                    }
                }).nullSafe();
            }
        }
    }
}
