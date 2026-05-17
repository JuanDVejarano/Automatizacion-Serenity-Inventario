@IA
Feature: HU-19 MCP de gestion de productos
  Como usuario con rol Produccion o Administrador
  Quiero interactuar con el MCP de productos
  Para consultar productos, verificar materiales, generar opciones de compra,
  validar presupuesto y crear ordenes de compra

  # ── TOOL 1: Listar productos ──────────────────────────────────────────────

  Scenario: Tool 1 - Listar todos los productos
    Given el MCP de productos esta disponible y conectado a la base de datos
    When se ejecuta la tool listar_productos sin parametros
    Then el sistema retorna la lista completa de productos con id, nombre, tipo, caracteristicas, precio y stock
    And cada producto muestra el stock disponible correctamente

  @pendiente
  Scenario: Tool 1 - Listar productos cuando no hay ninguno registrado
    Given el MCP de productos esta disponible y conectado a la base de datos
    When se ejecuta la tool listar_productos sin parametros con lista vacia
    Then el sistema retorna una lista vacia sin errores

  # ── TOOL 2: Verificar materiales ─────────────────────────────────────────

  Scenario: Tool 2 - Verificar materiales con stock suficiente
    Given el MCP de productos esta disponible y conectado a la base de datos
    When se ejecuta la tool verificar_materiales_producto con un id de producto valido y una cantidad a fabricar
    Then el sistema retorna por cada material el nombre, cantidad necesaria, stock actual y cantidad faltante
    And el sistema indica que es posible producir la cantidad solicitada

  Scenario: Tool 2 - Verificar materiales con stock insuficiente
    Given el MCP de productos esta disponible y conectado a la base de datos
    When se ejecuta la tool verificar_materiales_producto con una cantidad mayor al stock disponible
    Then el sistema retorna los materiales faltantes con su cantidad insuficiente
    And el sistema indica que NO es posible producir la cantidad solicitada

  Scenario: Tool 2 - Verificar materiales de producto inexistente
    Given el MCP de productos esta disponible y conectado a la base de datos
    When se ejecuta la tool verificar_materiales_producto con un id de producto que no existe
    Then el sistema retorna el mensaje de error producto no encontrado

  # ── TOOL 3: Generar opciones de orden de compra ───────────────────────────

  @pendiente
  Scenario: Tool 3 - Sin materiales faltantes para producir
    Given el MCP de productos esta disponible y conectado a la base de datos
    When se ejecuta la tool generar_opciones_orden_compra y no hay materiales faltantes
    Then el sistema retorna el mensaje sin materiales faltantes

  Scenario: Tool 3 - Con materiales faltantes retorna dos estrategias
    Given el MCP de productos esta disponible y conectado a la base de datos
    When se ejecuta la tool generar_opciones_orden_compra con materiales faltantes
    Then el sistema retorna la Estrategia 1 con un solo proveedor de menor costo total
    And el sistema retorna la Estrategia 2 con el proveedor optimo por cada material

  @pendiente
  Scenario: Tool 3 - Sin proveedor que cubra todos los materiales
    Given el MCP de productos esta disponible y conectado a la base de datos
    When se ejecuta la tool generar_opciones_orden_compra y ningun proveedor cubre todos los materiales
    Then la Estrategia 1 retorna el mensaje sin proveedor unico
    And la Estrategia 2 sigue retornando el proveedor optimo por material

  # ── TOOL 4: Validar presupuesto ───────────────────────────────────────────

  Scenario: Tool 4 - Fondos suficientes en caja
    Given el MCP de productos esta disponible y conectado a la base de datos
    When se ejecuta la tool validar_presupuesto_caja con un costo menor al capital disponible
    Then el sistema retorna el capital disponible, el costo requerido y confirma que hay fondos suficientes
    And la diferencia entre capital y costo es positiva

  Scenario: Tool 4 - Fondos insuficientes en caja
    Given el MCP de productos esta disponible y conectado a la base de datos
    When se ejecuta la tool validar_presupuesto_caja con un costo mayor al capital disponible
    Then el sistema retorna que NO hay fondos suficientes
    And la diferencia entre capital y costo es negativa

  @pendiente
  Scenario: Tool 4 - Sin caja configurada en el sistema
    Given el MCP de productos esta disponible y conectado a la base de datos
    When se ejecuta la tool validar_presupuesto_caja sin que haya una caja registrada
    Then el sistema retorna el mensaje sin caja configurada

  # ── TOOL 5: Crear orden de compra ─────────────────────────────────────────

  Scenario: Tool 5 - Crear orden de compra exitosamente
    Given el MCP de productos esta disponible y conectado a la base de datos
    And hay presupuesto suficiente en caja para cubrir el costo de la orden
    When se ejecuta la tool crear_orden_compra con la lista de materiales y cantidades
    Then el sistema crea la orden con estado Pendiente
    And el sistema retorna el id de la orden, el costo total y el mensaje de confirmacion

  @pendiente
  Scenario: Tool 5 - Fondos insuficientes al crear orden
    Given el MCP de productos esta disponible y conectado a la base de datos
    When se ejecuta la tool crear_orden_compra con un costo que supera el capital en caja
    Then el sistema retorna el mensaje fondos insuficientes
    And no se crea ninguna orden de compra en la base de datos

  @pendiente
  Scenario: Tool 5 - Sin proveedor para algun material
    Given el MCP de productos esta disponible y conectado a la base de datos
    When se ejecuta la tool crear_orden_compra con un material que no tiene proveedor asociado
    Then el sistema retorna el mensaje sin proveedor para el material
    And no se crea ninguna orden de compra en la base de datos

  @pendiente
  Scenario: Tool 5 - Estado Pendiente no configurado
    Given el MCP de productos esta disponible y conectado a la base de datos
    When se ejecuta la tool crear_orden_compra sin que exista el estado Pendiente en la base de datos
    Then el sistema retorna el mensaje estado pendiente no encontrado
    And no se crea ninguna orden de compra en la base de datos
