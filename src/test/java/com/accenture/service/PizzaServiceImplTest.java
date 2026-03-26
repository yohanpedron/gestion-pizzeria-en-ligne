package com.accenture.service;

import com.accenture.mapper.PizzaMapper;
import com.accenture.model.Ingredient;
import com.accenture.model.Pizza;
import com.accenture.model.PizzaSize;
import com.accenture.repository.PizzaDao;
import com.accenture.service.dto.*;
import jakarta.persistence.EntityExistsException;
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
import java.util.Optional;
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

    /// //////////////////////////// TESTS ATTRIBUTS PIZZA ////////////////////////////////////////////

    @Test
    @DisplayName("Test addPizza() must fail when name is empty")
    void testAddPizzaFailEmptyName() {
        // un nom vide
        String name = "";
        Map<PizzaSize, Double> price = Map.of(PizzaSize.MEDIUM, 9.0);
        List<IngredientRequestDto> ingredients = List.of(new IngredientRequestDto("Tomate"));

        PizzaRequestDto dtoRequest = new PizzaRequestDto(name, price, ingredients);

        // le service doit refuser l'ajout
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> pizzaService.addPizza(dtoRequest),
                "addPizza must throw IllegalArgumentException when name is empty"
        );
    }

    @Test
    @DisplayName("Test addPizza() must fail when price map is empty")
    void testAddPizzaFailEmptyPrice() {
        // prix vide
        String name = "Margherita";
        Map<PizzaSize, Double> price = Map.of(); // vide
        List<IngredientRequestDto> ingredients = List.of(new IngredientRequestDto("Tomate"));

        PizzaRequestDto dtoRequest = new PizzaRequestDto(name, price, ingredients);

        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> pizzaService.addPizza(dtoRequest),
                "addPizza must throw IllegalArgumentException when price map is empty"
        );
    }

    @Test
    @DisplayName("addPizza() must fail when a price is negative")
    void addPizzaFailNegativePrice() {
        // un DTO avec un prix négatif
        PizzaRequestDto request = new PizzaRequestDto(
                "Margherita",
                Map.of(PizzaSize.MEDIUM, -5.0),
                List.of(new IngredientRequestDto("Tomate"))
        );

        // le service doit lancer une exception
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> pizzaService.addPizza(request),
                "Price must be positive"
        );
    }


    @Test
    @DisplayName("Test addPizza() must fail when ingredient list is empty")
    void testAddPizzaFailEmptyIngredientList() {
        // ingrédients vides
        String name = "Margherita";
        Map<PizzaSize, Double> price = Map.of(PizzaSize.MEDIUM, 9.0);
        List<IngredientRequestDto> ingredients = List.of(); // vide

        PizzaRequestDto dtoRequest = new PizzaRequestDto(name, price, ingredients);

        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> pizzaService.addPizza(dtoRequest),
                "addPizza must throw IllegalArgumentException when ingredient list is empty"
        );
    }


    @Test
    @DisplayName("deletePizza() must set active to false instead of deleting")
    void deletePizzaSoftDeleteValidTest() {
        // une pizza existante et active
        String name = "Margherita";
        Pizza existing = new Pizza();
        existing.setName(name);
        existing.setActive(true);

        // Mock DAO — la pizza existe
        Mockito.when(pizzaDao.findByNameIgnoreCase(Mockito.anyString()))
                .thenReturn(Optional.of(existing));

        // Mock DAO — save renvoie la pizza désactivée
        Pizza disabled = new Pizza();
        disabled.setName(name);
        disabled.setActive(false);

        Mockito.when(pizzaDao.save(Mockito.any(Pizza.class)))
                .thenReturn(disabled);

        Assertions.assertDoesNotThrow(() -> pizzaService.deletePizza(name));
        Assertions.assertFalse(disabled.isActive(), "Pizza should be marked inactive after delete");
    }



    /// //////////////////////////// TESTS VERIFICATION PIZZA ////////////////////////////////////////////

    @Test
    @DisplayName("Test when Pizza is persisted from valid input")
    void testAddPizzaValidInputOk() {
        // Je crée mes variables pour les tester
        PizzaServiceImpl spy = Mockito.spy(pizzaService);
        String name = "Margherita";
        Map<PizzaSize, Double> price = Map.of(PizzaSize.MEDIUM, 9.0);
        List<IngredientRequestDto> ingredientDtos = List.of(new IngredientRequestDto("Tomate"));

        // Je crée mon request comme s'il venait du controller
        PizzaRequestDto dtoRequest = new PizzaRequestDto(name, price, ingredientDtos);
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
    @DisplayName("Test when addPizza() is called with a name that already exists, must throw EntityExistsException")
    void testAddPizzaAlreadyExists() {
        // un nom déjà existant
        String name = "Margherita";
        Map<PizzaSize, Double> price = Map.of(PizzaSize.MEDIUM, 9.0);
        List<IngredientRequestDto> ingredientDtos = List.of(new IngredientRequestDto("Tomate"));

        PizzaRequestDto dtoRequest = new PizzaRequestDto(name, price, ingredientDtos);

        // simulation d'une pizza déjà existante en base
        Pizza existingPizza = new Pizza();
        existingPizza.setName(name);

        // Mock DAO — findByNameIgnoreCase renvoie une pizza existante
        Mockito.when(pizzaDao.findByNameIgnoreCase(Mockito.anyString()))
                .thenReturn(Optional.of(existingPizza));

        // le service doit refuser l'ajout
        Assertions.assertThrows(
                EntityExistsException.class,
                () -> pizzaService.addPizza(dtoRequest),
                "addPizza must throw EntityExistsException when pizza name already exists"
        );
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
    @DisplayName("deletePizza() must throw when pizza does not exist")
    void deletePizzaInvalidTest() {
        Mockito.when(pizzaDao.findByNameIgnoreCase(Mockito.anyString()))
                .thenReturn(Optional.empty());

        Assertions.assertThrows(
                EntityNotFoundException.class,
                () -> pizzaService.deletePizza("Inexistante")
        );
    }


    @Test
    @DisplayName("Test patchPizza() must update only provided fields")
    void patchPizzaValidTest() {
        // pizza existante
        String name = "Margherita";
        Pizza existing = new Pizza(UUID.randomUUID(), name, Map.of(PizzaSize.MEDIUM, 9.0), List.of(new Ingredient("Tomate", 10)), true);

        // PATCH : on change seulement le prix
        PizzaPatchRequestDto patch = new PizzaPatchRequestDto(null, Map.of(PizzaSize.MEDIUM, 12.0), null);

        // pizza mise à jour simulée
        Pizza updated = new Pizza(existing.getId(), existing.getName(), patch.price(), existing.getIngredients(), existing.isActive());

        PizzaResponseDto expected = new PizzaResponseDto(
                updated.getId(),
                updated.getName(),
                updated.getPrice(),
                List.of(new IngredientResponseDto(
                        existing.getIngredients().getFirst().getId(),
                        existing.getIngredients().getFirst().getName()
                )),
                updated.isActive()
        );

        Mockito.when(pizzaDao.findByNameIgnoreCase(Mockito.anyString()))
                .thenReturn(Optional.of(existing));

        Mockito.when(pizzaDao.save(Mockito.any(Pizza.class)))
                .thenReturn(updated);

        Mockito.when(pizzaMapper.toPizzaResponseDto(updated))
                .thenReturn(expected);

        // WHEN
        PizzaResponseDto returned = pizzaService.patchPizza(name, patch);

        // THEN
        Assertions.assertAll(
                () -> Assertions.assertEquals(12.0, returned.price().get(PizzaSize.MEDIUM)),
                () -> Assertions.assertEquals("Margherita", returned.name()),
                () -> Assertions.assertTrue(returned.active())
        );
    }

    @Test
    @DisplayName("Test patchPizza() must fail when pizza does not exist")
    void patchPizzaInvalidTest() {
        PizzaPatchRequestDto patch = new PizzaPatchRequestDto(null, null, null);

        Mockito.when(pizzaDao.findByNameIgnoreCase(Mockito.anyString()))
                .thenReturn(Optional.empty());

        Assertions.assertThrows(
                EntityNotFoundException.class,
                () -> pizzaService.patchPizza("Inexistante", patch)
        );
    }


}