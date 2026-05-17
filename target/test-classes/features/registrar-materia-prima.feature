@materiaPrima
Feature: HU-20 Registrar materia prima
  Como usuario con rol Produccion o Administrador
  Quiero registrar una nueva materia prima en el sistema
  Para mantener el inventario de insumos necesarios para la fabricacion de productos quirurgicos

  Background:
    Given el usuario ha iniciado sesion en el modulo de materia prima

  Scenario: Registro exitoso de materia prima
    When el usuario registra una materia prima con nombre valido
    Then el sistema muestra el mensaje de exito de materia prima "Materia prima registrada exitosamente"

  Scenario: Registro exitoso de materia prima con descripcion
    When el usuario registra una materia prima con nombre y descripcion
    Then el sistema muestra el mensaje de exito de materia prima "Materia prima registrada exitosamente"

  Scenario: Nombre de materia prima duplicado
    Given existe una materia prima registrada en el sistema
    When el usuario intenta registrar una materia prima con el mismo nombre
    Then el sistema muestra el mensaje de error de materia prima "Ya existe una materia prima con ese nombre"

  Scenario: Campo nombre vacio
    When el usuario intenta registrar una materia prima sin diligenciar el nombre
    Then el sistema muestra el mensaje de error de materia prima "El nombre de la materia prima es obligatorio"

  Scenario: Modulo no visible para roles no autorizados
    Given el usuario inicia sesion con el rol "Ventas"
    When el usuario intenta acceder al modulo de registro de materia prima
    Then el sistema muestra la pagina de acceso denegado
    And el sistema muestra el mensaje "No tiene permisos para acceder a este modulo"
