@IA
Feature: HU-29 MCP de finanzas
  Como usuario con rol Tesoreria o Administrador
  Quiero interactuar con el MCP de finanzas
  Para analizar el estado financiero de la organizacion mediante consultas, reportes y proyecciones

  # ── TOOL 1: Consultar estado actual de caja ───────────────────────────────

  Scenario: Tool 1 - Consultar estado actual de caja
    Given el MCP de finanzas esta disponible y conectado a la base de datos
    When se ejecuta la tool consultar_estado_caja
    Then el sistema retorna el capital disponible, fecha del ultimo movimiento, total de ingresos y egresos historicos

  # ── TOOL 2: Consultar historial de movimientos ────────────────────────────

  Scenario: Tool 2 - Consultar historial de movimientos con filtros
    Given el MCP de finanzas esta disponible y conectado a la base de datos
    When se ejecuta la tool consultar_historial_movimientos con filtros opcionales
    Then el sistema retorna la lista de movimientos que coincidan con los filtros aplicados

  # ── TOOL 3: Analizar ingresos vs egresos ─────────────────────────────────

  Scenario: Tool 3 - Analizar ingresos vs egresos por periodo
    Given el MCP de finanzas esta disponible y conectado a la base de datos
    When se ejecuta la tool analizar_ingresos_egresos con una fecha de inicio y fin
    Then el sistema retorna el total de ingresos, egresos, balance neto y desglose por tipo de movimiento

  # ── TOOL 4: Proyeccion de flujo de caja ──────────────────────────────────

  Scenario: Tool 4 - Proyeccion de flujo de caja por meses
    Given el MCP de finanzas esta disponible y conectado a la base de datos
    When se ejecuta la tool proyectar_flujo_caja con el numero de meses a proyectar
    Then el sistema retorna la proyeccion de ingreso, egreso y saldo estimado por cada mes

  # ── TOOL 5: Reporte de rentabilidad por producto ──────────────────────────

  Scenario: Tool 5 - Reporte de rentabilidad por producto
    Given el MCP de finanzas esta disponible y conectado a la base de datos
    When se ejecuta la tool reporte_rentabilidad_productos
    Then el sistema retorna el ranking de productos con unidades vendidas e ingresos generados

  # ── TOOL 6: Detectar deficit o superavit ─────────────────────────────────

  Scenario: Tool 6 - Detectar meses con deficit o superavit
    Given el MCP de finanzas esta disponible y conectado a la base de datos
    When se ejecuta la tool detectar_deficit_superavit con el anio a analizar
    Then el sistema retorna la clasificacion de deficit o superavit por cada mes del anio

  # ── TOOL 7: Resumen financiero general ───────────────────────────────────

  Scenario: Tool 7 - Resumen financiero general del negocio
    Given el MCP de finanzas esta disponible y conectado a la base de datos
    When se ejecuta la tool resumen_financiero_general
    Then el sistema retorna capital actual, totales historicos de ventas y ordenes, y balance general
