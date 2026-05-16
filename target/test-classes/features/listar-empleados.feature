@empleados
Feature: HU-06 Listar y buscar empleados
  Como usuario con rol Recursos Humanos o Administrador
  Quiero listar y buscar empleados en el sistema
  Para consultar la informacion del personal de la organizacion

  Background:
    Given el administrador ha iniciado sesion para consulta de empleados

  Scenario: Listar todos los empleados con sus datos
    Given existe al menos un empleado registrado en el sistema
    When el usuario navega al modulo de listado de empleados
    Then el sistema muestra la tabla con las columnas de informacion de empleados
    And la tabla contiene al menos un empleado registrado

  Scenario: Buscar empleado por nombre
    Given existe un empleado registrado para busqueda en la lista
    When el usuario busca el empleado por nombre en la lista
    Then el sistema muestra el empleado en los resultados de busqueda

  Scenario: Busqueda por nombre sin resultados
    When el usuario navega al modulo de listado de empleados
    And el usuario busca por "XYZNOEXISTE99999" en la lista de empleados
    Then el sistema muestra en la lista el mensaje "No se encontraron empleados con ese criterio de busqueda"

  Scenario: Buscar empleado por cedula
    Given existe un empleado registrado para busqueda en la lista
    When el usuario busca el empleado por cedula en la lista
    Then el sistema muestra exactamente un empleado en la tabla de resultados

  Scenario: Busqueda por cedula sin resultados
    When el usuario navega al modulo de listado de empleados
    And el usuario busca por "00000001" en la lista de empleados
    Then el sistema muestra en la lista el mensaje "No se encontraron empleados con ese criterio de busqueda"

  Scenario: Filtrar empleados por estado activo
    Given existe al menos un empleado registrado en el sistema
    When el usuario navega al modulo de listado de empleados
    And el usuario filtra los empleados por estado "activos"
    Then todos los empleados mostrados tienen el estado Activo

  Scenario: Lista vacia de empleados
    When el usuario navega al modulo de listado de empleados
    And se simula que no hay empleados en el sistema
    Then el sistema muestra en la lista el mensaje "No hay empleados registrados"

  Scenario: Acceso denegado para roles no autorizados al modulo de empleados
    Given el usuario inicia sesion con el rol "Ventas"
    When el usuario intenta acceder al modulo de lista de empleados
    Then el sistema muestra la pagina de acceso denegado
    And el sistema muestra el mensaje "No tiene permisos para acceder a este modulo"
