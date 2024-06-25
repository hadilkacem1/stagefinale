package com.github.akhuntsaria.apigateway.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class UserDetailsDeserializer extends JsonDeserializer<UserDetails> {

    @Override
    public UserDetails deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);

        String username = node.get("username").asText();
        String password = node.get("password").asText(); // Assurez-vous que le champ 'password' est présent dans votre réponse JSON
        boolean enabled = node.get("enabled").asBoolean(); // Assurez-vous que le champ 'enabled' est présent dans votre réponse JSON

        // Désérialise les rôles de l'utilisateur
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        JsonNode rolesNode = node.get("roles");
        if (rolesNode != null) {
            for (JsonNode roleNode : rolesNode) {
                authorities.add(new SimpleGrantedAuthority(roleNode.asText()));
            }
        }

        return new User(username, password, enabled, true, true, true, authorities);
    }
}

