package com.selenium;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        /*//Creating webdriver instance
        WebDriver driver1= new ChromeDriver();
        driver1.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
        //Open web application
        driver1.get("https://elenastepuro.github.io/test_env/index.html");
        driver1.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
        //Enter data into textboxes
        driver1.findElement(By.id("change_id")).sendKeys("test_id");
        driver1.findElement(By.id("change_className")).sendKeys("test_class");
        driver1.findElement(By.id("Submit")).click();
        driver1.quit();*/

/*
     ------------------------------------------------------------------------------------
*/

        // Set up ChromeDriver
        //System.setProperty("webdriver.chrome.driver", "C:/Users/Bahadyr/Java project directory/chromedriver.exe");
        ChromeOptions options = new ChromeOptions();
        WebDriver driver = new ChromeDriver(options);

        try {
            // Open Trendyol website
            driver.get("https://www.trendyol.com/");

            // Close the pop-up
            closePopup(driver);

            // Click on the "Best Sellers" link
            clickBestSellerLink(driver);

            // Get products
            List<WebElement> products = getProducts(driver);

            for (int i = 0; i < Math.min(products.size(), 30); i++) {
                // Re-fetch the products list to avoid stale element reference exception
                products = getProducts(driver);

                // Click on the product link
                products.get(i).click();

                // Get product information
                String[] productInfo = getProductInfo(driver);
                String productTitle = productInfo[0];
                String productPrice = productInfo[1];
                boolean isDiscounted = Boolean.parseBoolean(productInfo[2]);

                if (isDiscounted) {
                    System.out.println("Product " + (i + 1) + " title: " + productTitle + ", Discounted Price: " + productPrice);
                    addToCart(driver);
                } else {
                    System.out.println("Product " + (i + 1) + " title: " + productTitle + ", Price: " + productPrice);
                }

                // Go back to the product listing page
                driver.navigate().back();
                new WebDriverWait(driver, Duration.ofSeconds(10)).until(
                        ExpectedConditions.visibilityOfElementLocated(By.className("product-listing-container"))
                );
            }

            // Open the cart
            openCart(driver);

        } finally {
            // Quit the driver
            driver.quit();
        }
    }

    private static void closePopup(WebDriver driver) {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            WebElement popUpCloseButton = wait.until(
                    ExpectedConditions.elementToBeClickable(By.cssSelector("div.modal-close"))
            );
            popUpCloseButton.click();
        } catch (Exception e) {
            System.out.println("Failed to close pop-up: " + e.getMessage());
        }
    }

    private static void clickBestSellerLink(WebDriver driver) {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            WebElement bestSellerLink = wait.until(
                    ExpectedConditions.elementToBeClickable(By.xpath("//a[@href='/cok-satanlar?type=bestSeller&webGenderId=1']"))
            );
            bestSellerLink.click();
        } catch (Exception e) {
            System.out.println("Failed to click on the 'Best Sellers' link: " + e.getMessage());
        }
    }

    private static List<WebElement> getProducts(WebDriver driver) {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            wait.until(
                    ExpectedConditions.visibilityOfElementLocated(By.className("product-listing-container"))
            );
            return driver.findElements(By.cssSelector(".product-card a"));
        } catch (Exception e) {
            System.out.println("Failed to get products: " + e.getMessage());
            return List.of();
        }
    }

    private static String[] getProductInfo(WebDriver driver) {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            wait.until(
                    ExpectedConditions.visibilityOfElementLocated(By.tagName("h1"))
            );
            String productTitle = driver.findElement(By.tagName("h1")).getText();

            try {
                String discountPrice = driver.findElement(By.cssSelector(".featured-prices .prc-dsc")).getText();
                return new String[]{productTitle, discountPrice, "true"};
            } catch (Exception e) {
                String regularPrice = driver.findElement(By.cssSelector(".prc-dsc")).getText();
                return new String[]{productTitle, regularPrice, "false"};
            }
        } catch (Exception e) {
            System.out.println("Failed to get product info: " + e.getMessage());
            return new String[]{null, null, "false"};
        }
    }

    private static void addToCart(WebDriver driver) {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            WebElement addToCartButton = wait.until(
                    ExpectedConditions.elementToBeClickable(By.cssSelector("button.add-to-basket"))
            );
            addToCartButton.click();
            wait.until(
                    ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div.add-to-basket-button-text-success"))
            );
            System.out.println("Product has been added to the cart.");
        } catch (Exception e) {
            System.out.println("Failed to add product to cart: " + e.getMessage());
        }
    }

    private static void openCart(WebDriver driver) {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            WebElement cartLink = wait.until(
                    ExpectedConditions.elementToBeClickable(By.cssSelector("a[href='/sepet']"))
            );
            cartLink.click();
            Thread.sleep(5000); // Time for inspecting the cart
        } catch (Exception e) {
            System.out.println("Failed to open cart: " + e.getMessage());
        }
    }
}