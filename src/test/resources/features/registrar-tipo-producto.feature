@produccion
Feature: HU-16 Registrar tipo de producto
  Como usuario con rol Produccion o Administrador
  Quiero registrar un nuevo tipo de producto en el sistema
  Para clasificar y organizar los productos de la organizacion

  Background:
    Given el usuario ha iniciado sesion en el modulo de produccion

  Scenario: Registro exitoso de tipo de producto
    When el usuario registra un tipo de producto con nombre unico
    Then el sistema muestra el mensaje de exito de tipo de producto "Tipo de producto registrado exitosamente"

  Scenario: Nombre de tipo de producto duplicado
    Given el usuario registra previamente un tipo de producto
    When el usuario intenta registrar otro tipo de producto con el mismo nombre
    Then el sistema muestra el mensaje de error de tipo de producto "Ya existe un tipo de producto con ese nombre"

  Scenario: Campo nombre vacio
    When el usuario intenta registrar un tipo de producto sin nombre
    Then el sistema muestra el mensaje de error de tipo de producto "El nombre del tipo de producto es obligatorio"

  Scenario: Acceso no autorizado al modulo de tipos de producto
    Given el usuario inicia sesion con el rol "Ventas"
    When el usuario intenta acceder al modulo de tipos de producto
    Then el sistema muestra la pagina de acceso denegado
    And el sistema muestra el mensaje "No tiene permisos para acceder a este modulo"
