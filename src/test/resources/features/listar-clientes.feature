@clientes
Feature: HU-12 Listar y buscar clientes
  Como usuario con rol Ventas o Administrador
  Quiero listar y buscar clientes en el sistema
  Para consultar la informacion y seleccionarlos al momento de registrar una venta

  Background:
    Given el usuario ha iniciado sesion en el modulo de clientes

  Scenario: Listar todos los clientes con sus datos
    Given existe al menos un cliente registrado en el sistema
    When el usuario navega al modulo de listado de clientes
    Then el sistema muestra la tabla de clientes con las columnas requeridas
    And la tabla contiene al menos un cliente registrado

  Scenario: Buscar cliente por nombre
    Given existe un cliente registrado para busqueda en la lista de clientes
    When el usuario busca el cliente por nombre en la lista de clientes
    Then el sistema muestra el cliente en los resultados de busqueda de clientes

  Scenario: Busqueda por nombre sin resultados
    When el usuario navega al modulo de listado de clientes
    And el usuario busca por "XYZNOEXISTECLIENTE99" en la lista de clientes
    Then el sistema muestra en la lista de clientes el mensaje "No se encontraron clientes con ese criterio de busqueda"

  Scenario: Buscar cliente por cedula o NIT
    Given existe un cliente registrado para busqueda en la lista de clientes
    When el usuario busca el cliente por cedula en la lista de clientes
    Then el sistema muestra exactamente un cliente en la tabla de resultados de clientes

  Scenario: Busqueda por cedula sin resultados
    When el usuario navega al modulo de listado de clientes
    And el usuario busca por "00000001" en la lista de clientes
    Then el sistema muestra en la lista de clientes el mensaje "No se encontraron clientes con ese criterio de busqueda"

  @pendiente
  Scenario: Lista vacia de clientes
    When el usuario navega al modulo de listado de clientes
    And se simula que no hay clientes en el sistema
    Then el sistema muestra en la lista de clientes el mensaje "No hay clientes registrados"

  Scenario: Acceso no autorizado al modulo de listado de clientes
    Given el usuario inicia sesion con el rol "Produccion"
    When el usuario intenta acceder al modulo de clientes
    Then el sistema muestra la pagina de acceso denegado
    And el sistema muestra el mensaje "No tiene permisos para acceder a este modulo"
