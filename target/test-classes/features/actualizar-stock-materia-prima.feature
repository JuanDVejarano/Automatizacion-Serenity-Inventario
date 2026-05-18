@materiaPrima
Feature: HU-24 Actualizar stock de materia prima
  Como usuario con rol Produccion o Administrador
  Quiero actualizar el stock de materia prima manualmente
  Para reflejar las entradas y salidas de insumos del proceso de produccion

  Background:
    Given el usuario accede al modulo de actualizar stock de materia prima

  Scenario: Aumentar stock de materia prima manualmente
    When el usuario aumenta el stock de la primera materia prima en 10 unidades
    Then el sistema muestra el mensaje de exito de actualizacion de stock "Stock actualizado exitosamente"

  Scenario: Reducir stock de materia prima manualmente
    Given la materia prima tiene stock disponible para reducir
    When el usuario reduce el stock de la primera materia prima en 5 unidades
    Then el sistema muestra el mensaje de exito de actualizacion de stock "Stock actualizado exitosamente"

  Scenario: Stock insuficiente para reducir
    When el usuario intenta reducir el stock en una cantidad mayor al disponible
    Then el sistema muestra el mensaje de error de actualizacion de stock "Stock insuficiente para realizar la reduccion"

  Scenario: Cantidad invalida al actualizar stock
    When el usuario intenta actualizar el stock con cantidad cero
    Then el sistema muestra el mensaje de error de actualizacion de stock "La cantidad debe ser mayor a 0"

  Scenario: Modulo no visible para roles no autorizados
    Given el usuario inicia sesion con el rol "Ventas"
    When el usuario intenta acceder al modulo de actualizar stock de materia prima
    Then el sistema muestra la pagina de acceso denegado
    And el sistema muestra el mensaje "No tiene permisos para acceder a este modulo"
