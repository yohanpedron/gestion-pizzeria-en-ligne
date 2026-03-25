package com.accenture.service;

import com.accenture.mapper.PizzaMapper;
import com.accenture.model.Ingredient;
import com.accenture.model.Pizza;
import com.accenture.model.PizzaSize;
import com.accenture.repository.PizzaDao;
import com.accenture.service.dto.IngredientRequestDto;
import com.accenture.service.dto.IngredientResponseDto;
import com.accenture.service.dto.PizzaRequestDto;
import com.accenture.service.dto.PizzaResponseDto;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class PizzaServiceImplTest {

    @Mock
    private PizzaDao pizzaDao;

    @Mock
    private PizzaMapper pizzaMapper;

    @InjectMocks
    private PizzaServiceImpl pizzaService;

    @BeforeEach
    void setUp() {
        //MockitoExtension + InjectMocks s'occupent de tout
    }

    @Test
    @DisplayName("Test when Pizza is persisted from valid input")
    void testAddPizzaValidInputOk() {
        // Je crée mes variables pour les tester
        PizzaServiceImpl spy = Mockito.spy(pizzaService);
        String name = "Margherita";
        Map<PizzaSize, Double> price = Map.of(PizzaSize.MEDIUM, 9.0);
        List<IngredientRequestDto> ingredientDtos = List.of(new IngredientRequestDto("Tomate"));

        // Je crée mon request comme s'il venait du controller
        PizzaRequestDto dtoRequest = new PizzaRequestDto(name, price, ingredientDtos, true);
        // Je crée mon entité (avant sauvegarde)
        Ingredient ingredientEntity = new Ingredient("Tomate", 10);
        Pizza pizza= new Pizza(name, price, List.of(ingredientEntity), true);

        // Je crée mon entité sauvegardée (avec ID)
        Ingredient savedIngredient = new Ingredient(UUID.randomUUID(), "Tomate", 10);
        Pizza savedPizza = new Pizza(UUID.randomUUID(), name, price, List.of(savedIngredient), true);
        // Je crée ma response souhaitée
        PizzaResponseDto returnedResponse = new PizzaResponseDto(savedPizza.getId(), savedPizza.getName(), savedPizza.getPrice(), List.of(new IngredientResponseDto(savedIngredient.getId(), savedIngredient.getName())), true);

        // Je mock les méthodes spécifiques utilisées par la méthode que je veux tester
        Mockito.when(pizzaMapper.toPizza(Mockito.any(PizzaRequestDto.class))).thenReturn(pizza);
        Mockito.when(pizzaDao.save(Mockito.any(Pizza.class))).thenReturn(savedPizza);
        Mockito.when(pizzaMapper.toPizzaResponseDto(Mockito.any(Pizza.class))).thenReturn(returnedResponse);

        //j'appelle la méthode du service
        PizzaResponseDto returnedValue = spy.addPizza(dtoRequest);

        // Vérification des appels internes
        Assertions.assertAll(()
                -> Assertions.assertNotNull(returnedValue, "DtoResponse should not be null"), ()
                -> Assertions.assertNotNull(returnedValue.id(), "Id should not be null"), ()
                -> Assertions.assertNotNull(returnedValue.name(), "Name should not be null"), ()
                -> Assertions.assertEquals(name, returnedValue.name(), "Name should be the same as the expected"));
        Mockito.verify(spy, Mockito.times(1)).addPizza(Mockito.any(PizzaRequestDto.class));
    }

    @Test
    @DisplayName("Test the method findAll() from service, must return correct output")
    void TestFindAllValid() {
        // Je créé mes variables pour les tester
        Ingredient ingredient1 = new Ingredient(UUID.randomUUID(), "Tomate", 10);
        Ingredient ingredient2 = new Ingredient(UUID.randomUUID(), "Mozzarella", 10);
        Pizza pizza1 = new Pizza(UUID.randomUUID(), "Margherita", Map.of(PizzaSize.MEDIUM, 9.0), List.of(ingredient1), true);
        Pizza pizza2 = new Pizza(UUID.randomUUID(), "Regina", Map.of(PizzaSize.LARGE, 12.0), List.of(ingredient2), true);

        List<Pizza> pizzaList = List.of(pizza1, pizza2);

        // DTOs attendus
        PizzaResponseDto dto1 = new PizzaResponseDto(pizza1.getId(), pizza1.getName(), pizza1.getPrice(), List.of(new IngredientResponseDto(ingredient1.getId(), ingredient1.getName())), true);
        PizzaResponseDto dto2 = new PizzaResponseDto(pizza2.getId(), pizza2.getName(), pizza2.getPrice(), List.of(new IngredientResponseDto(ingredient2.getId(), ingredient2.getName())), true);

        List<PizzaResponseDto> expectedList = List.of(dto1, dto2);

        // Mock DAO
        Mockito.when(pizzaDao.findAll()).thenReturn(pizzaList);

        // Mock Mapper
        Mockito.when(pizzaMapper.toPizzaResponseDto(pizza1)).thenReturn(dto1);
        Mockito.when(pizzaMapper.toPizzaResponseDto(pizza2)).thenReturn(dto2);

        List<PizzaResponseDto> returnedList = pizzaService.findAll();

        Assertions.assertAll(
                () -> Assertions.assertNotNull(returnedList, "Returned list should not be null"),
                () -> Assertions.assertEquals(2, returnedList.size(), "List size should match expected"),
                () -> Assertions.assertEquals(expectedList.getFirst().id(), returnedList.getFirst().id(), "First pizza ID should match"),
                () -> Assertions.assertEquals(expectedList.getFirst().name(), returnedList.getFirst().name(), "First pizza name should match"),
                () -> Assertions.assertEquals(expectedList.get(1).id(), returnedList.get(1).id(), "Second pizza ID should match"),
                () -> Assertions.assertEquals(expectedList.get(1).name(), returnedList.get(1).name(), "Second pizza name should match")
        );
    }

    @Test
    @DisplayName("Test the method findAll() from service, must return empty list when no pizzas exist")
    void TestFindAllEmptyList() {
        List<Pizza> emptyList = List.of();

        // Mock DAO
        Mockito.when(pizzaDao.findAll()).thenReturn(emptyList);

        List<PizzaResponseDto> returnedList = pizzaService.findAll();

        Assertions.assertAll(
                () -> Assertions.assertNotNull(returnedList, "Returned list should not be null"),
                () -> Assertions.assertTrue(returnedList.isEmpty(), "Returned list should be empty")
        );
    }

    @Test
    @DisplayName("Test the method findByName() from service, must return correct output")
    void TestfindByNameValid() {
        //recherche sur les 3 premières lettres du nom
        String inputName = "mar";

        //simulation des ingrédients présents dans les pizzas
        Ingredient ing1 = new Ingredient(UUID.randomUUID(), "Tomate", 10);
        Ingredient ing2 = new Ingredient(UUID.randomUUID(), "Mozzarella", 10);

        //simulation des entités Pizza renvoyées par la DAO
        Pizza pizza1 = new Pizza(UUID.randomUUID(), "Margherita", Map.of(PizzaSize.MEDIUM, 9.0), List.of(ing1), true);
        Pizza pizza2 = new Pizza(UUID.randomUUID(), "Marine", Map.of(PizzaSize.SMALL, 8.0), List.of(ing2), true);

        List<Pizza> pizzaList = List.of(pizza1, pizza2);

        // DTOs attendus après passage par le mapper
        PizzaResponseDto dto1 = new PizzaResponseDto(pizza1.getId(), pizza1.getName(), pizza1.getPrice(), List.of(new IngredientResponseDto(ing1.getId(), ing1.getName())), true);
        PizzaResponseDto dto2 = new PizzaResponseDto(pizza2.getId(), pizza2.getName(), pizza2.getPrice(), List.of(new IngredientResponseDto(ing2.getId(), ing2.getName())), true);

        // Mock DAO — renvoie les entités simulées
        Mockito.when(pizzaDao.findByNameContainingIgnoreCase(Mockito.anyString())).thenReturn(pizzaList);

        // Mock Mapper — convertit les entités en DTOs
        Mockito.when(pizzaMapper.toPizzaResponseDto(pizza1)).thenReturn(dto1);
        Mockito.when(pizzaMapper.toPizzaResponseDto(pizza2)).thenReturn(dto2);

        List<PizzaResponseDto> returnedList = pizzaService.findByName(inputName);

        Assertions.assertAll(
                () -> Assertions.assertNotNull(returnedList, "Returned list should not be null"),
                () -> Assertions.assertEquals(2, returnedList.size(), "List size should match expected"),
                () -> Assertions.assertEquals("Margherita", returnedList.getFirst().name(), "First pizza name should match"),
                () -> Assertions.assertEquals("Marine", returnedList.get(1).name(), "Second pizza name should match")
        );
    }

    @Test
    @DisplayName("Test the method findByName() from service, must return empty list when no pizza matches")
    void TestfindByNameEmptyResult() {
        // recherche sur un nom qui ne correspond à aucune pizza
        String inputName = "xyz";

        // la DAO renvoie une liste vide
        Mockito.when(pizzaDao.findByNameContainingIgnoreCase(Mockito.anyString()))
                .thenReturn(List.of());

        List<PizzaResponseDto> returnedList = pizzaService.findByName(inputName);

        Assertions.assertAll(
                () -> Assertions.assertNotNull(returnedList, "Returned list should not be null"),
                () -> Assertions.assertTrue(returnedList.isEmpty(), "Returned list should be empty")
        );
    }


    @Test
    @DisplayName("Test the method findByIngredient() from service, must return correct output")
    void TestfindByIngredientValid() {
        //recherche sur les 3 premières lettres d'un ingrédient
        String ingredientSearch = "tom";

        // simulation des ingrédients présents dans les pizzas
        Ingredient ing1 = new Ingredient(UUID.randomUUID(), "Tomate", 10);
        Ingredient ing2 = new Ingredient(UUID.randomUUID(), "Tomate fraîche", 10);

        // simulation des entités Pizza renvoyées par la DAO
        Pizza pizza1 = new Pizza(UUID.randomUUID(), "Margherita", Map.of(PizzaSize.MEDIUM, 9.0), List.of(ing1), true);
        Pizza pizza2 = new Pizza(UUID.randomUUID(), "Napolitaine", Map.of(PizzaSize.LARGE, 12.0), List.of(ing2), true);

        List<Pizza> pizzaList = List.of(pizza1, pizza2);

        // DTOs attendus après passage par le mapper
        PizzaResponseDto dto1 = new PizzaResponseDto(pizza1.getId(), pizza1.getName(), pizza1.getPrice(), List.of(new IngredientResponseDto(ing1.getId(), ing1.getName())), true);
        PizzaResponseDto dto2 = new PizzaResponseDto(pizza2.getId(), pizza2.getName(), pizza2.getPrice(), List.of(new IngredientResponseDto(ing2.getId(), ing2.getName())), true);

        // Mock DAO — renvoie les entités simulées
        Mockito.when(pizzaDao.findByIngredientsNameContainingIgnoreCase(Mockito.anyString())).thenReturn(pizzaList);

        // Mock Mapper — convertit les entités en DTOs
        Mockito.when(pizzaMapper.toPizzaResponseDto(pizza1)).thenReturn(dto1);
        Mockito.when(pizzaMapper.toPizzaResponseDto(pizza2)).thenReturn(dto2);

        List<PizzaResponseDto> returnedList = pizzaService.findByIngredient(ingredientSearch);

        Assertions.assertAll(
                () -> Assertions.assertNotNull(returnedList, "Returned list should not be null"),
                () -> Assertions.assertEquals(2, returnedList.size(), "List size should match expected"),
                () -> Assertions.assertTrue(returnedList.getFirst().name().contains("Mar"), "First pizza name should match expected"),
                () -> Assertions.assertTrue(returnedList.get(1).name().contains("Nap"), "Second pizza name should match expected")
        );
    }
    @Test
    @DisplayName("Test the method findByIngredient() from service, must return empty list when no pizza contains the ingredient")
    void TestfindByIngredientEmptyResult() {
        // recherche sur un ingrédient inexistant
        String ingredientSearch = "chocolat";

        // la DAO renvoie une liste vide
        Mockito.when(pizzaDao.findByIngredientsNameContainingIgnoreCase(Mockito.anyString()))
                .thenReturn(List.of());

        List<PizzaResponseDto> returnedList = pizzaService.findByIngredient(ingredientSearch);

        Assertions.assertAll(
                () -> Assertions.assertNotNull(returnedList, "Returned list should not be null"),
                () -> Assertions.assertTrue(returnedList.isEmpty(), "Returned list should be empty")
        );
    }

    @Test
    @DisplayName("Test the method delete() from service, must delete pizza without error")
    void testDeletePizzaWithoutError() {
        UUID id = UUID.randomUUID();

        //Simulation de la pizza existante
        Pizza pizza = new Pizza();
        pizza.setId(id);

        //Mock DAO - getReferenceById renvoie l'entité
        Mockito.when(pizzaDao.getReferenceById(Mockito.any(UUID.class))).thenReturn(pizza);

        //Appel de la méthode
        Assertions.assertDoesNotThrow(() -> pizzaService.deletePizza(id));

        //On vérifie que ça ne plante pas
        Assertions.assertAll(
                () -> Assertions.assertDoesNotThrow(() -> pizzaService.deletePizza(id),"Delete should not throw any exception for valid ID")
        );
    }

    @Test
    @DisplayName("Test the method delete() from service, must throw EntityNotFoundException when pizza does not exist")
    void deleteInvalidTest() {
        // un ID inexistant
        UUID id = UUID.randomUUID();

        // Mock DAO — getReferenceById lance une exception
        Mockito.when(pizzaDao.getReferenceById(Mockito.any(UUID.class)))
                .thenThrow(EntityNotFoundException.class);

        // on vérifie que l’exception remonte
        Assertions.assertThrows(
                EntityNotFoundException.class,
                () -> pizzaService.deletePizza(id),
                "Delete must throw EntityNotFoundException when pizza does not exist"
        );
    }



}