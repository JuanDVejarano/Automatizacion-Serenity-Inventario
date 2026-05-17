@usuarios
Feature: HU-08 Asignar rol a usuario
  Como usuario con rol Recursos Humanos o Administrador
  Quiero asignar o cambiar el rol de un usuario existente
  Para ajustar los permisos de acceso segun las responsabilidades del empleado

  Background:
    Given el administrador ha iniciado sesion para gestion de usuarios

  Scenario: Asignacion exitosa de rol
    Given existe un usuario registrado para la prueba de asignacion de rol
    When el usuario abre el modal de asignacion de rol del primer usuario
    And el usuario selecciona un rol diferente al actual
    And el usuario confirma la asignacion de rol
    Then el sistema muestra el mensaje de exito de asignacion "Rol asignado exitosamente"

  Scenario: Intento de asignar el mismo rol actual
    Given existe un usuario registrado para la prueba de asignacion de rol
    When el usuario abre el modal de asignacion de rol del primer usuario
    And el usuario confirma la asignacion de rol
    Then el sistema muestra el mensaje de error de asignacion "El usuario ya tiene ese rol asignado"

  Scenario: Seleccion de rol vacia al asignar
    Given existe un usuario registrado para la prueba de asignacion de rol
    When el usuario abre el modal de asignacion de rol del primer usuario
    And el usuario limpia la seleccion de rol en el modal
    And el usuario confirma la asignacion de rol
    Then el sistema muestra el mensaje de error de asignacion "Debe seleccionar un rol"

  Scenario: Acceso no autorizado al modulo de gestion de usuarios
    Given el usuario inicia sesion con el rol "Ventas"
    When el usuario intenta acceder al modulo de gestion de usuarios
    Then el sistema muestra la pagina de acceso denegado
    And el sistema muestra el mensaje "No tiene permisos para acceder a este modulo"
