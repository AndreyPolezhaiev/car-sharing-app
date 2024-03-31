## **Description:**

Project "Car-Sharing-App" was created to solve the problem of the offline using the service.
Now Managers get messages about new rentals created or success payments in CarRentalBot online.
They have information about actions of users including successful payments, cancelled payments,
created rentals, overdue rentals.

## **Technologies:**

Spring Boot, Spring Security, Spring Data JPA, Stripe API, Telegram API, Hibernate, Java Core, OOP, Docker, Maven, Git and SQL.

## **Controllers:**

There are controllers, that allow to get all cars, add car to the rental,
search rentals by specific parameters, create the payment session and mark it successful or cancelled
and also register a user, and log in.
Also, there are others for creating a car, set actual return date of the rental that is allowed for managers only.

## **For people:**

People using GitHub can pull my project and make some corrections, improve the project, or use it for their goals.

## **Problems:**

Making a "Car-Sharing-App" I faced to make payment functionality and telegram functionality.
I found another programmers' solutions about stripe and remade a functionality for my requirements.
Regarding telegram, I learnt how to create a bot and remade it for car-sharing-app.

## **Postman queries:**

##### There are basic Postman queries that are explained below.

### **Basic Postman queries for users:**

#### GET:

`http://localhost:8082/api/cars` - Get all cars

`http://localhost:8082/api/cars/` - Get all cars with detailed information

`http://localhost:8082/api/cars/1` - Get the car by id

`http://localhost:8082/api/rentals/my` - Get users' rental

`http://localhost:8082/api/payments` - Get all users' rental

`http://localhost:8082/api/payments/status?status=PENDING` - Get all users' payments by status


#### POST:

`http://localhost:8082/api/rentals` - Create a rental
```json
      {
          "rentalDate": "2024-03-09T17:36:00",
          "returnDate": "2024-04-09T17:36:00",
          "actualReturnDate": "",
          "carId": 1
      }
```

`http://localhost:8082/api/payments/pay` - Create a payment
```json
      {
          "rentalId": 1,
          "paymentType": "FINE"
      }
```

`http://localhost:8082/api/payments/success?sessionId=cs_test_a1jY8xw5XKsuhUYlnMPyNGMcY7MXnaq5icUB6tyPBwEkv7EmDi1cFZVL1F`
- Set successful payment

`http://localhost:8082/api/payments/cancel?sessionId=cs_test_a10Dh82wOHcB6VAD5s8g2JVif2vlQpyi9YGx1pSuznnlKhEXEoel695StH`
- Set cancelled payment

`http://localhost:8082/auth/registration` - Register a user
```json
      {
          "email": "ben@gmail.com",
          "password": "123456", 
          "repeatPassword": "123456", 
          "firstName": "Ben", 
          "lastName": "Osten"
      }
```

`http://localhost:8082/auth/login` - Login a user
```json
      {
          "email": "ben@gmail.com",
          "password": "1234",
          "repeatPassword": "1234"
      }
```
### Basic postman queries for managers:

#### POST:

`http://localhost:8080/api/books` - Create a car
```json
      {
          "model": "Camry",
          "brand": "Toyota",
          "typeName": "SEDAN",
          "dailyFee": 6.5,
          "inventory": 1
      }
```

`http://localhost:8082/api/rentals/return/1` - Set actual rentals return date
```json
      {
          "actualDate": "2024-05-09T17:36:00"
      }
```

#### PUT:

`http://localhost:8082/api/cars/1` - Update the car
```json
      {
          "model": "Portofino M",
          "brand": "Ferrari",
          "typeName": "Sedan",
          "dailyFee": 50,
          "inventory": 1
      }
```

#### DELETE:

`http://localhost:8082/api/cars/1` - Delete the car 

## **Loom video with instruction to my project**
https://www.loom.com/share/bda145fe42b546adb915df997b1ae8f4?sid=0a24e000-fda1-4fc7-b190-3bea5b554989