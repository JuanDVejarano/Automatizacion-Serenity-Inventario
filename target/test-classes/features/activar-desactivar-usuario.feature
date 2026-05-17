@usuarios
Feature: HU-09 Activar y desactivar usuario
  Como usuario con rol Recursos Humanos o Administrador
  Quiero activar o desactivar usuarios del sistema
  Para controlar el acceso sin eliminar el registro del usuario

  Background:
    Given el administrador ha iniciado sesion para gestion de usuarios

  Scenario: Desactivar un usuario activo
    Given existe un usuario de prueba activo para la gestion de estado
    When el administrador desactiva al usuario de prueba
    Then el usuario de prueba aparece como Inactivo en la lista de usuarios

  Scenario: Activar un usuario inactivo
    Given existe un usuario de prueba inactivo para la gestion de estado
    When el administrador activa al usuario de prueba
    Then el usuario de prueba aparece como Activo en la lista de usuarios

  Scenario: Acceso no autorizado al modulo de activacion de usuarios
    Given el usuario inicia sesion con el rol "Ventas"
    When el usuario intenta acceder al modulo de gestion de usuarios
    Then el sistema muestra la pagina de acceso denegado
    And el sistema muestra el mensaje "No tiene permisos para acceder a este modulo"
