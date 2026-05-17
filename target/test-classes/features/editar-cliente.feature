@clientes
Feature: HU-11 Editar informacion de cliente
  Como usuario con rol Ventas o Administrador
  Quiero editar la informacion de un cliente
  Para mantener los datos actualizados y corregir posibles errores

  Background:
    Given el usuario ha iniciado sesion en el modulo de clientes

  Scenario: Edicion exitosa de informacion general del cliente
    Given existe un cliente registrado para la prueba de edicion
    When el usuario busca y abre el modal de edicion del cliente
    And el usuario actualiza el nombre del cliente a "Nombre Cliente Editado"
    And el usuario guarda los cambios del cliente
    Then el sistema muestra el mensaje de exito de edicion de cliente "Cliente actualizado exitosamente"

  Scenario: Edicion de cedula o NIT por Administrador
    Given existe un cliente registrado para la prueba de edicion
    When el usuario busca y abre el modal de edicion del cliente
    And el Administrador cambia la cedula del cliente por un valor nuevo
    And el usuario guarda los cambios del cliente
    Then el sistema muestra el mensaje de exito de edicion de cliente "Cliente actualizado exitosamente"

  Scenario: Campo cedula deshabilitado para rol Ventas
    Given existe un cliente registrado para la prueba de edicion
    And se establece el token de sesion con rol Ventas para clientes
    When el usuario busca y abre el modal de edicion del cliente
    Then el campo cedula o NIT esta deshabilitado en el modal de edicion del cliente

  Scenario: Correo duplicado al editar cliente
    Given existen dos clientes registrados para la prueba de duplicados en edicion
    When el usuario abre el modal del primer cliente e ingresa el correo del segundo cliente
    Then el sistema muestra el mensaje de error de edicion de cliente "Ya existe un cliente con ese correo"

  Scenario: Cedula duplicada al editar cliente
    Given existen dos clientes registrados para la prueba de duplicados en edicion
    When el usuario abre el modal del primer cliente e ingresa la cedula del segundo cliente
    Then el sistema muestra el mensaje de error de edicion de cliente "Ya existe un cliente con esa cedula"

  Scenario: Campos obligatorios vacios al editar cliente
    Given existe un cliente registrado para la prueba de edicion
    When el usuario busca y abre el modal de edicion del cliente
    And el usuario borra los campos obligatorios del modal de edicion del cliente
    And el usuario guarda los cambios del cliente
    Then el sistema muestra el mensaje de error de edicion de cliente "Todos los campos obligatorios deben ser diligenciados"

  Scenario: Acceso no autorizado al modulo de edicion de clientes
    Given el usuario inicia sesion con el rol "Produccion"
    When el usuario intenta acceder al modulo de clientes
    Then el sistema muestra la pagina de acceso denegado
    And el sistema muestra el mensaje "No tiene permisos para acceder a este modulo"
