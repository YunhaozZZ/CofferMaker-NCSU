package edu.ncsu.csc.CoffeeMaker.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import edu.ncsu.csc.CoffeeMaker.security.SecurityProperties;

/**
 * Controller class for the URL mappings for CoffeeMaker. The controller returns
 * the approprate HTML page in the /src/main/resources/templates folder. For a
 * larger application, this should be split across multiple controllers.
 *
 * @author Kai Presler-Marshall
 */
@Controller
public class MappingController {

    /** Access to information about roles */
    @Autowired
    private SecurityProperties props;

    /**
     * On a GET request to /index, the IndexController will return
     * /src/main/resources/templates/index.html.
     *
     * @return contents of the page
     */
    @GetMapping ( { "/index", "/" } )
    public String index ( @AuthenticationPrincipal final Authentication auth ) {
        final GrantedAuthority grantedAuthority = auth.getAuthorities() //
                .stream() //
                .findFirst() //
                .get();

        final SecurityProperties.Role foundRole = props.getRoles() //
                .stream() //
                .filter( role -> role.getName().equals( grantedAuthority.getAuthority() ) ).findFirst() //
                .get();

        return "redirect:" + foundRole.getHomePage();
    }

    /**
     * On a GET request to /index, the IndexController will return
     * /src/main/resources/templates/index.html.
     *
     * @param model
     *            underlying UI model
     * @return contents of the page
     */
    @GetMapping ( { "/customerhome", "/customerhome.html" } )
    public String customerhome ( final Model model ) {
        return "customerhome";
    }

    /**
     * On a GET request to /index, the IndexController will return
     * /src/main/resources/templates/index.html.
     *
     * @param model
     *            underlying UI model
     * @return contents of the page
     */
    @GetMapping ( { "/staffhome", "/staffhome.html" } )
    public String staffhome ( final Model model ) {
        return "staffhome";
    }

    /**
     * On a GET request to /recipe, the RecipeController will return
     * /src/main/resources/templates/recipe.html.
     *
     * @param model
     *            underlying UI model
     * @return contents of the page
     */
    @GetMapping ( { "/recipe", "/recipe.html" } )
    public String addRecipePage ( final Model model ) {
        return "recipe";
    }

    /**
     * On a GET request to /deleterecipe, the DeleteRecipeController will return
     * /src/main/resources/templates/deleterecipe.html.
     *
     * @param model
     *            underlying UI model
     * @return contents of the page
     */
    @GetMapping ( { "/deleterecipe", "/deleterecipe.html" } )
    public String deleteRecipeForm ( final Model model ) {
        return "deleterecipe";
    }

    /**
     * On a GET request to /editrecipe, the EditRecipeController will return
     * /src/main/resources/templates/editrecipe.html.
     *
     * @param model
     *            underlying UI model
     * @return contents of the page
     */
    @GetMapping ( { "/editrecipe", "/editrecipe.html" } )
    public String editRecipeForm ( final Model model ) {
        return "editrecipe";
    }

    /**
     * Handles a GET request for inventory. The GET request provides a view to the
     * client that includes the list of the current ingredients in the inventory and
     * a form where the client can enter more ingredients to add to the inventory.
     *
     * @param model
     *            underlying UI model
     * @return contents of the page
     */
    @GetMapping ( { "/inventory", "/inventory.html" } )
    public String inventoryForm ( final Model model ) {
        return "inventory";
    }

    /**
     * On a GET request to /makecoffee, the MakeCoffeeController will return
     * /src/main/resources/templates/makecoffee.html.
     *
     * @param model
     *            underlying UI model
     * @return contents of the page
     */
    @GetMapping ( { "/makecoffee", "/makecoffee.html" } )
    public String makeCoffeeForm ( final Model model ) {
        return "makecoffee";
    }

    /**
     * On a GET request to /addingredients, the AddIngredientsController will return
     * /src/main/resources/templates/addingredients.html.
     *
     * @param model
     *            underlying UI model
     * @return contents of the page
     */
    @GetMapping ( { "/addingredients", "/addingredients.html" } )
    public String addIngredientsForm ( final Model model ) {
        return "addingredients";
    }

    /**
     * On a GET request to /addingredients, the AddIngredientsController will return
     * /src/main/resources/templates/addingredients.html.
     *
     * @param model
     *            underlying UI model
     * @return contents of the page
     */
    @GetMapping ( { "/stafforderview", "/stafforderview.html" } )
    public String stafforderviewForm ( final Model model ) {
        return "stafforderview";
    }

    /**
     * On a post request to /addOrders, the addOrdersController will return
     * /src/main/resources/templates/order.html.
     *
     * @param model
     *            underlying UI model
     * @return contents of the page
     */
    @GetMapping ( { "/order", "/order.html" } )
    public String orderForm ( final Model model ) {
        return "order";
    }

    /**
     * On a get request to /reviewOrders, the ordersController will return
     * /src/main/resources/templates/revieworder.html.
     *
     * @param model
     *            underlying UI model
     * @return contents of the page
     */
    @GetMapping ( { "/revieworder", "/revieworder.html" } )
    public String reviewOrderForm ( final Model model ) {
        return "revieworder";
    }

    /**
     * On a GET request to /getCustomerOrders, the OrderController will return
     * /src/main/resources/templates/customerorderreview.html.
     *
     * @param model
     *            underlying UI model
     * @return contents of the page
     */
    @GetMapping ( { "/customerorderview", "/customerorderview.html" } )
    public String customerorderviewForm ( final Model model ) {
        return "customerorderview";
    }

    /**
     * On a GET request to /about, the OrderController will return
     * /src/main/resources/templates/about.html.
     *
     * @param model
     *            underlying UI model
     * @return contents of the page
     */
    @GetMapping ( { "/about", "/about.html" } )
    public String aboutForm ( final Model model ) {
        return "about";
    }

    /**
     * On a GET request to /vieworder, the OrderController will return
     * /src/main/resources/templates/about.html.
     *
     * @param model
     *            underlying UI model
     * @return contents of the page
     */
    @GetMapping ( { "/vieworder", "/vieworder.html" } )
    public String vieworderForm ( final Model model ) {
        return "vieworder";
    }

    /**
     * On a GET request to /login, the OrderController will return
     * /src/main/resources/templates/login.html.
     *
     * @param model
     *            underlying UI model
     * @return contents of the page
     */
    @GetMapping ( { "/login", "/login.html" } )
    public String viewLoginForm ( final Model model ) {
        return "login";
    }
}
