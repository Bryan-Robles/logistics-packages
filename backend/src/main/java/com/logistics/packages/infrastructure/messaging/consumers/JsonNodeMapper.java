package com.logistics.packages.infrastructure.messaging.consumers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;

/**
 * Utility class for safe JSON node mapping to handle cross-account SQS payloads
 * without strict class validation.
 */
public class JsonNodeMapper {

    // JavaTimeModule: payloads carry OffsetDateTime/LocalDateTime fields.
    // Unknown properties are ignored: other modules may add fields to their events.
    private static final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

    /**
     * Convierte un payload de SQS al tipo esperado.
     * - Si ya es del tipo esperado (mensaje con header de tipo), se usa tal cual.
     * - Si llega como String (JSON crudo de otra cuenta AWS), se parsea.
     * - En otro caso (p. ej. Map), se convierte vía árbol JSON.
     */
    public static <T> T convert(Object payload, Class<T> targetClass) {
        if (targetClass.isInstance(payload)) {
            return targetClass.cast(payload);
        }
        try {
            if (payload instanceof String json) {
                return objectMapper.readValue(json, targetClass);
            }
            return objectMapper.convertValue(payload, targetClass);
        } catch (Exception e) {
            throw new RuntimeException("Error al convertir payload a " + targetClass.getSimpleName(), e);
        }
    }

    /**
     * Convierte cualquier objeto Java a JsonNode
     */
    public static <T> JsonNode toJsonNode(T object) {
        try {
            return objectMapper.valueToTree(object);
        } catch (Exception e) {
            throw new RuntimeException("Error al convertir objeto a JsonNode", e);
        }
    }

    /**
     * Convierte un JsonNode a cualquier clase Java
     */
    public static <T> T fromJsonNode(JsonNode jsonNode, Class<T> targetClass) {
        try {
            return objectMapper.treeToValue(jsonNode, targetClass);
        } catch (Exception e) {
            throw new RuntimeException("Error al convertir JsonNode a " + targetClass.getSimpleName(), e);
        }
    }

    /**
     * Convierte un JsonNode a un objeto con manejo de excepciones seguro
     */
    public static <T> T fromJsonNode(JsonNode jsonNode, Class<T> targetClass, T defaultValue) {
        try {
            return objectMapper.treeToValue(jsonNode, targetClass);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    /**
     * Combina dos JsonNode (útil para merges)
     */
    public static JsonNode merge(JsonNode source, JsonNode target) {
        ObjectNode result = objectMapper.createObjectNode();

        try {
            objectMapper.readerForUpdating(result).readValue(source);
            objectMapper.readerForUpdating(result).readValue(target);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    /**
     * Obtiene el ObjectMapper si necesitas operaciones custom
     */
    public static ObjectMapper getObjectMapper() {
        return objectMapper;
    }
}