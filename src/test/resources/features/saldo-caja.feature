@caja
Feature: HU-26 Consultar saldo de caja
  Como usuario con rol Tesoreria o Administrador
  Quiero consultar el saldo actual de la caja
  Para conocer el capital disponible de la organizacion en tiempo real

  Background:
    Given el usuario accede al modulo de saldo de caja

  Scenario: Consulta exitosa del saldo de caja
    Then el sistema muestra la tarjeta de saldo con el capital disponible

  Scenario: Saldo de caja en cero
    When se simula que el capital de la caja es cero
    Then el sistema muestra la alerta de que no hay capital disponible

  Scenario: No hay caja configurada en el sistema
    When se simula que no hay caja configurada
    Then el sistema muestra el mensaje de sin caja "No hay caja configurada en el sistema, contacte al administrador"

  Scenario: Modulo no visible para roles no autorizados
    Given el usuario inicia sesion con el rol "Ventas"
    When el usuario intenta acceder al modulo de saldo de caja
    Then el sistema muestra la pagina de acceso denegado
    And el sistema muestra el mensaje "No tiene permisos para acceder a este modulo"
