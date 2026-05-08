package com.project.artconnect;

import com.project.artconnect.util.ConnectionManager;

import java.sql.Connection;

public class TestConnection {

    public static void main(String[] args) {

        try (Connection connection = ConnectionManager.getConnection()) {

            if (connection != null) {
                System.out.println("Connexion réussie !");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}