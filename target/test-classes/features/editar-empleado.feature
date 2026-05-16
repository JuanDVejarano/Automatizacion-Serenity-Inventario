Feature: HU-05 Editar informacion de empleado
  Como usuario con rol Recursos Humanos o Administrador
  Quiero editar la informacion de un empleado
  Para mantener los datos del personal actualizados y corregir posibles errores

  Background:
    Given el administrador ha iniciado sesion para edicion de empleados

  Scenario: Edicion exitosa de informacion general
    Given existe un empleado registrado para la prueba de edicion
    When el usuario busca y abre el modal de edicion del empleado
    And el usuario actualiza el nombre en el modal a "Nombre Actualizado Test"
    And el usuario guarda los cambios en el modal
    Then el sistema muestra el mensaje de exito de edicion "Empleado actualizado exitosamente"

  Scenario: Edicion de cedula por Administrador
    Given existe un empleado registrado para la prueba de edicion
    When el usuario busca y abre el modal de edicion del empleado
    And el Administrador cambia la cedula del empleado por un valor nuevo
    And el usuario guarda los cambios en el modal
    Then el sistema muestra el mensaje de exito de edicion "Empleado actualizado exitosamente"

  Scenario: Campo cedula deshabilitado para rol Recursos Humanos
    Given existe un empleado registrado para la prueba de edicion
    And se establece el token de sesion con rol Recursos Humanos
    When el usuario busca y abre el modal de edicion del empleado
    Then el campo cedula esta deshabilitado en el modal de edicion

  Scenario: Correo duplicado al editar
    Given existen dos empleados registrados para la prueba de duplicados en edicion
    When el usuario abre el modal del primer empleado e ingresa el correo del segundo
    Then el sistema muestra el mensaje de error de edicion "Ya existe un empleado con ese correo"

  Scenario: Cedula duplicada al editar
    Given existen dos empleados registrados para la prueba de duplicados en edicion
    When el usuario abre el modal del primer empleado e ingresa la cedula del segundo
    Then el sistema muestra el mensaje de error de edicion "Ya existe un empleado con esa cedula"

  Scenario: Campos obligatorios vacios al editar
    Given existe un empleado registrado para la prueba de edicion
    When el usuario busca y abre el modal de edicion del empleado
    And el usuario borra los campos obligatorios del modal de edicion
    And el usuario guarda los cambios en el modal
    Then el sistema muestra el mensaje de error de edicion "Todos los campos obligatorios deben ser diligenciados"

  Scenario: Acceso no autorizado al modulo de lista de empleados
    Given el usuario inicia sesion con el rol "Ventas"
    When el usuario intenta acceder al modulo de lista de empleados
    Then el sistema muestra la pagina de acceso denegado
    And el sistema muestra el mensaje "No tiene permisos para acceder a este modulo"
