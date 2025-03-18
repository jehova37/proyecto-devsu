# Proyecto - Sistema Demostrativo utilizando Microservicios

## Arquitectura
- **Servicio-Clientes**: Este servicio maneja la creación, consulta, actualización y eliminación de clientes (CRUD), 
  tambien envia eventos o mensajes a una cola JMS(ActiveMQ) al crear un cliente.
- **Servicio-Cuenta-Movimiento**: se encarga de recibir el mensaje o evento que envio el servicio servicio-clientes para crear la cuenta, tambien por medio de endpoints 
  puede gestionar los movimientos de las cuentas.
- **Bases de Datos**: Cada servicio usa una base de datos H2, para motivos de pruebas de este proyecto.
- **Cola JMS**: ActiveMQ es el broker de comunicacion utilizado para la comunicacion asincronica entre ambos servicios.

## Requisitos
- Java 18 (o compatible, como JDK 18.0.2.1)
- Maven 3.8+
- ActiveMQ 

## Instalación y Ejecución

1. **Clonar el Repositorio**
git clone https://github.com/jehova37/proyecto-devsu

ir al directorio del proyecto
cd proyecto-devsu

2. **Subir el broker ActiveMQ**
- en windows apache-activemq\bin\activemq start

- en Linux o Max ./apache-activemq/bin/activemq start

3. **Compilar el proyecto**
mvn clean install
(esto compila ambos servicios y ejecuta las pruebas unitarias)


4. **subir el servicio de servicio-clientes**
java -jar servicio-clientes/target/servicio-clientes-1.0.0.jar

5. **subir el servicio de servicio-cuenta-movimiento**
java -jar servicio-cuenta-movimiento/target/servicio-cuenta-movimiento-1.0.0.jar



- **ENDPOINTS DE LOS MICROSERVICIOS**

--- servicio-clientes --> url base (http://localhost:8080)

- **crear nuevo cliente**
(BODY o Cuerpo a enviar en formato JSON)
POST /clientes
Content-Type: application/json


{
    "nombre": "Jose Lema",
    "genero": "Masculino",
    "edad": 30,
    "identificacion": "1234567890",
    "direccion": "Otavalo sn y principal",
    "telefono": "098254785",
    "clienteId": "CLI001",
    "contraseña": "1234",
    "estado": true
}


- **Listar clientes 
GET /clientes

- **Obtener un cliente por ID
GET /clientes/{id}

- **Actualizar un cliente por ID
PUT /clientes/{id}

- **Eliminar un cliente por ID
DELETE /clientes/{id}

- **Obtener el nombre de un cliente por ID
GET /clientes/nombre/{id}



- **servicio-cuenta-movimiento URL base --> (http://localhost:8098)**

- **Registrar un movimiento en la cuenta**
POST /movimientos?cuentaId=1&tipoMovimiento=DEPOSITO&valor=100.00
POST /movimientos?cuentaId=1&tipoMovimiento=RETIRO&valor=100.00

- **Generar reporte**
GET /movimientos/reportes?clienteId=1&fechaInicio=2025-03-17T00:00:00&fechaFin=2025-03-18T23:59:59


- **Pruebas Unitarias general**
en la raiz del directorio principal ejecutar
-- mvn test
 
- **pruebas unitarias en el servicio servicio-clientes**
-- cd servicio-clientes
-- mvn test


- **pruebas unitarias en servicio servicio-cuenta-movimiento**
-- cd servicio-cuenta-movimiento
-- mvn test



