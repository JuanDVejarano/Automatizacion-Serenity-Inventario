@caja
Feature: HU-27 Registrar movimiento de caja
  Como usuario con rol Tesoreria o Administrador
  Quiero registrar movimientos manuales en la caja
  Para reflejar ingresos y egresos no asociados a ventas u ordenes de compra

  Background:
    Given el usuario accede al modulo de registrar movimiento de caja

  Scenario: Registrar gasto operativo
    When el usuario registra un movimiento de tipo "Gasto operativo" con valor 5000
    Then el sistema muestra el mensaje de exito del movimiento "Movimiento registrado exitosamente"

  Scenario: Registrar ingreso externo
    When el usuario registra un movimiento de tipo "Ingreso externo" con valor 10000
    Then el sistema muestra el mensaje de exito del movimiento "Movimiento registrado exitosamente"

  Scenario: Inyeccion de capital por Administrador
    When el usuario registra un movimiento de tipo "Inyeccion de capital" con valor 50000
    Then el sistema muestra el mensaje de exito del movimiento "Capital inyectado exitosamente"

  Scenario: Deduccion de saldo por Administrador
    When el usuario registra un movimiento de tipo "Deduccion de saldo" con valor 1000
    Then el sistema muestra el mensaje de exito del movimiento "Deduccion registrada exitosamente"

  Scenario: Capital insuficiente para gasto o deduccion
    When el usuario intenta registrar un gasto operativo con valor mayor al capital disponible
    Then el sistema muestra el mensaje de error del movimiento "Capital insuficiente"

  Scenario: Valor invalido en movimiento de caja
    When el usuario intenta registrar un movimiento con valor cero
    Then el sistema muestra el error de validacion del movimiento "El valor del movimiento debe ser mayor a 0"

  Scenario: Inyeccion y deduccion no visibles para Tesoreria
    When se simula el acceso al modulo con rol Tesoreria
    Then el tipo de movimiento "Inyeccion de capital" no debe aparecer en el formulario
    And el tipo de movimiento "Deduccion de saldo" no debe aparecer en el formulario
    And el formulario debe mostrar solo los tipos de movimiento permitidos para Tesoreria

  Scenario: Modulo no visible para roles no autorizados
    Given el usuario inicia sesion con el rol "Produccion"
    When el usuario intenta acceder al modulo de registrar movimiento
    Then el sistema muestra la pagina de acceso denegado
    And el sistema muestra el mensaje "No tiene permisos para acceder a este modulo"
