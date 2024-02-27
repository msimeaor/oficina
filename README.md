**OBS.: Como estou treinando inglês, decidi escrever o README em inglês, mas para facilitar a leitura de visitantes, 
deixarei a tradução de cada texto para o português também. Todos os textos escritos em inglês ficarão em itálico
e começarão com o simbolo "->". Cada texto em ingles ficara abaixo do texto de tradução para o português.**

# Oficina API

Este é um projeto criado com fins educacionais. Ele representa uma API para uma oficina mecânica.
A API oferece serviços como: Registro e gerenciamento de clientes, veículos, serviços e vendas.

*-> This is a project created for educational purposes. It represents an API for a machine workshop.
The API offers services such as: Registration and management of customers, vehicles, services and sales.*

A seguir, irei usar imagens da documentação da API desenvolvida com a biblioteca do Swagger API para explicar cada
endpoint em detalhes

*-> Below, I will use images from the API documentation developed with the Swagger API library to explain each endpoint in detail.*

## PessoaRestController (Customer)

Este é o primeiro controller que irei explicar. A imagem abaixo mostra todos os endpoints do controller de pessoa (clientes).

*-> This is the first controller I will explain. The image below shows all the person controller endpoints (customers)*

![ ](aplicacao/src/main/resources/documentation.images/Pessoa_Endpoints.png)

**O controller de pessoa possui 5 endpoints sendo eles:**

***-> The person controller has 5 endpoints:***

### Salvando novo cliente (Saving a new customer) "/api/pessoas/{placa}"

Este endpoint POST recebe um path param (placa do carro) e um objeto no corpo da requisição.
A API vai validar os valores do objeto usando a biblioteca Jakarta.validation.constraints e validar a placa do carro
A logica para salvar o cliente é: Se houver um cliente no banco de dados que tenha o mesmo nome (nome completo) que o
cliente que está sendo salvo e se houver um veiculo no banco de dados que contenha a mesma placa que foi passada
no request param, a API entende que este cliente é repetido e retorna um erro. Caso contrário, o cliente é salvo com
sucesso.

*-> This POST endpoint receives a request param (car plate) and an object in the request body.
The API will validate the object's values using the Jakarta.validation.constraints library and validate the car plate
The logic for saving a new customer is: If there is a customer in the database that has the same name (full name)
as the customer being saved and if there is a vehicle in the database that contains the same car plate that was passed
in request param, the API understands that this customer is retried and returns an error. Otherwise, the
customer is saved with success*

![ ](aplicacao/src/main/resources/documentation.images/Pessoa_Save_Endpoint.png)

**Respostas**

***Responses***

- 201 OK: Caso o cliente seja salvo com sucesso, a API retorna um objeto contendo os dados do cliente.
- *-> 201 OK: If the customer is saved successfully, the API returns an object containing the customer's data.*

- 409 CONFLICT: Se a validação dos dados e placa falharem, a API irá retornar um objeto contendo detalhes do erro
- *-> 409 CONFLICT: If the data validation fail, the API will return an object containing the error details*

![ ](aplicacao/src/main/resources/documentation.images/Pessoa_Save_Endpoint_Success_Response.png)

![ ](aplicacao/src/main/resources/documentation.images/Pessoa_Save_Endpoint_Conflict_Response.png)

---

### Buscando cliente pelo ID (Find a customer by ID) "/api/pessoas/{id}"

Ao chamar esse endpoint, o usuário passa o ID em um path param.
A API vai buscar um cliente no banco de dados que tenha esse mesmo ID. Se achar, retorna os dados do cliente, caso
contrário, um 404 NOT FOUND será retornado com o mesmo objeto que foi retornado no 409 do endpoint anterior, mas com
os detalhes atualizados.

*-> When calling this endpoint, the user passes the ID in a path param.
The API will search for a customer in the database that has the same ID. If found, return the customer data, otherwise, 
a 404 NOT FOUND will be returned with the same object that was returned in the 409 of the previous endpoint, but with the
updated details.*

![ ](aplicacao/src/main/resources/documentation.images/Pessoa_FindById_Endpoint_Success_Response.png)