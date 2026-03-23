package com.pr0f1t.comparo.adminservice.client;

import com.pr0f1t.comparo.adminservice.exception.KeycloakOperationException;
import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class KeycloakAdminClient {

    private final Keycloak keycloak;

    @Value("${keycloak.realm}")
    public String realm;

    public List<UserRepresentation> getUsers() {
        try{
            return keycloak.realm(realm).users().list();
        }catch(Exception e){
            throw new KeycloakOperationException("Error retrieving user list from Keycloak", e);
        }
    }

    public int countUsers() {
        try {
            return keycloak.realm(realm).users().count();
        } catch (Exception e) {
            throw new KeycloakOperationException("Error counting users in Keycloak", e);
        }
    }

    public void updateUserStatus(String userId, boolean enabled) {
        try {
            var userResource = keycloak.realm(realm).users().get(userId);
            UserRepresentation user = userResource.toRepresentation();
            user.setEnabled(enabled);
            userResource.update(user);

        }catch (NotFoundException e){
            throw new KeycloakOperationException("User with ID: " + userId + " not found", e);
        }catch (Exception e){
            throw new KeycloakOperationException("Error communicating with Keycloak", e);
        }
    }

    public void updateUserProfile(String userId, String firstName, String lastName) {
        try {
            var userResource = keycloak.realm(realm).users().get(userId);
            UserRepresentation user = userResource.toRepresentation();
            user.setFirstName(firstName);
            user.setLastName(lastName);
            userResource.update(user);

        } catch (NotFoundException e) {
            throw new KeycloakOperationException("User with ID: " + userId + " not found", e);
        } catch (Exception e) {
            throw new KeycloakOperationException("Error updating user profile in Keycloak", e);
        }
    }
}
