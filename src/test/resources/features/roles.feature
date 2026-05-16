@roles
Feature: HU-03 Roles predefinidos del sistema
  Como administrador del sistema
  Quiero que el sistema tenga roles predefinidos
  Para controlar el acceso a los modulos segun la funcion de cada usuario

  Scenario Outline: Los modulos del dashboard reflejan el acceso segun el rol asignado
    Given el usuario inicia sesion con el rol "<rol>"
    When el usuario accede al dashboard
    Then el modulo "<modulo>" esta "<estado>" para ese rol

    Examples:
      | rol              | modulo               | estado        |
      | Administrador    | Ventas               | habilitado    |
      | Administrador    | Gestion de Empleados | habilitado    |
      | Administrador    | Finanzas             | habilitado    |
      | Administrador    | Produccion           | habilitado    |
      | Ventas           | Ventas               | habilitado    |
      | Ventas           | Gestion de Empleados | deshabilitado |
      | Tesoreria        | Finanzas             | habilitado    |
      | Tesoreria        | Ventas               | deshabilitado |
      | Produccion       | Produccion           | habilitado    |
      | Produccion       | Ventas               | deshabilitado |
      | Recursos Humanos | Gestion de Empleados | habilitado    |
      | Recursos Humanos | Finanzas             | deshabilitado |

  Scenario: Los roles no pueden gestionarse desde la interfaz
    Given el usuario inicia sesion con el rol "Administrador"
    When el usuario accede al dashboard
    Then no existe una opcion de gestion de roles en el sistema

  Scenario Outline: Acceso denegado al intentar acceder a un modulo no permitido
    Given el usuario inicia sesion con el rol "<rol>"
    When el usuario intenta acceder directamente a la ruta "<ruta>"
    Then el sistema muestra la pagina de acceso denegado
    And el sistema muestra el mensaje "No tiene permisos para acceder a este modulo"

    Examples:
      | rol              | ruta              |
      | Ventas           | /caja/saldo       |
      | Ventas           | /rrhh/empleados   |
      | Tesoreria        | /ventas/ventas    |
      | Tesoreria        | /rrhh/empleados   |
      | Produccion       | /ventas/ventas    |
      | Produccion       | /rrhh/empleados   |
      | Recursos Humanos | /ventas/ventas    |
      | Recursos Humanos | /caja/saldo       |
