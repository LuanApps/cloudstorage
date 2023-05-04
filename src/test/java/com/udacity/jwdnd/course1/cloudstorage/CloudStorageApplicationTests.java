package com.udacity.jwdnd.course1.cloudstorage;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;

import javax.validation.constraints.AssertTrue;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CloudStorageApplicationTests {

	@LocalServerPort
	private int port;

	private WebDriver driver;

	@BeforeAll
	static void beforeAll() {
		WebDriverManager.chromedriver().setup();
	}

	@BeforeEach
	public void beforeEach() {
		this.driver = new ChromeDriver();
	}

	@AfterEach
	public void afterEach() {
		if (this.driver != null) {
			driver.quit();
		}
	}

	@Test
	public void getLoginPage() {
		driver.get("http://localhost:" + this.port + "/login");
		assertEquals("Login", driver.getTitle());
	}

	/**
	 * PLEASE DO NOT DELETE THIS method.
	 * Helper method for Udacity-supplied sanity checks.
	 **/
	private void doMockSignUp(String firstName, String lastName, String userName, String password){
		// Create a dummy account for logging in later.

		// Visit the sign-up page.
		WebDriverWait webDriverWait = new WebDriverWait(driver, 2);
		driver.get("http://localhost:" + this.port + "/signup");
		webDriverWait.until(ExpectedConditions.titleContains("Sign Up"));
		
		// Fill out credentials
		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("inputFirstName")));
		WebElement inputFirstName = driver.findElement(By.id("inputFirstName"));
		inputFirstName.click();
		inputFirstName.sendKeys(firstName);

		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("inputLastName")));
		WebElement inputLastName = driver.findElement(By.id("inputLastName"));
		inputLastName.click();
		inputLastName.sendKeys(lastName);

		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("inputUsername")));
		WebElement inputUsername = driver.findElement(By.id("inputUsername"));
		inputUsername.click();
		inputUsername.sendKeys(userName);

		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("inputPassword")));
		WebElement inputPassword = driver.findElement(By.id("inputPassword"));
		inputPassword.click();
		inputPassword.sendKeys(password);

		// Attempt to sign up.
		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("buttonSignUp")));
		WebElement buttonSignUp = driver.findElement(By.id("buttonSignUp"));
		buttonSignUp.click();

		/* Check that the sign up was successful.
		// You may have to modify the element "success-msg" and the sign-up
		// success message below depening on the rest of your code.
		*/
		Assertions.assertTrue(driver.findElement(By.id("success-msg")).getText().contains("You successfully signed up!"));
	}

	/**
	 * PLEASE DO NOT DELETE THIS method.
	 * Helper method for Udacity-supplied sanity checks.
	 **/
	private void doLogIn(String userName, String password)
	{
		// Log in to our dummy account.
		driver.get("http://localhost:" + this.port + "/login");
		WebDriverWait webDriverWait = new WebDriverWait(driver, 2);

		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("inputUsername")));
		WebElement loginUserName = driver.findElement(By.id("inputUsername"));
		loginUserName.click();
		loginUserName.sendKeys(userName);

		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("inputPassword")));
		WebElement loginPassword = driver.findElement(By.id("inputPassword"));
		loginPassword.click();
		loginPassword.sendKeys(password);

		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("login-button")));
		WebElement loginButton = driver.findElement(By.id("login-button"));
		loginButton.click();

		webDriverWait.until(ExpectedConditions.titleContains("Home"));

	}

	/**
	 * PLEASE DO NOT DELETE THIS TEST. You may modify this test to work with the 
	 * rest of your code. 
	 * This test is provided by Udacity to perform some basic sanity testing of 
	 * your code to ensure that it meets certain rubric criteria. 
	 * 
	 * If this test is failing, please ensure that you are handling redirecting users 
	 * back to the login page after a succesful sign up.
	 * Read more about the requirement in the rubric: 
	 * https://review.udacity.com/#!/rubrics/2724/view 
	 */

	@Test
	public void testSuccesfullLoginAfterSignUp(){
		doMockSignUp("teste","teste","teste","teste");
		doLogIn("teste","teste");
		driver.get("http://localhost:" + this.port + "/home");
		assertEquals("Home", driver.getTitle());

	}
	@Test
	public void testRedirectionAfterSuccesfulSignUp() {
		// Create a test account
		doMockSignUp("Redirection","Test","RT","123");

		WebDriverWait webDriverWait = new WebDriverWait(driver, 2);
		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("login-link")));
		WebElement loginButton = driver.findElement(By.id("login-link"));
		loginButton.click();
		
		// Check if we have been redirected to the log in page.
		assertEquals("http://localhost:" + this.port + "/login", driver.getCurrentUrl());
	}

	@Test
	public void testUnauthorizedUserCantAccessHome(){
		driver.get("http://localhost:" + this.port + "/login");
		assertEquals("http://localhost:" + this.port + "/login", driver.getCurrentUrl());

		driver.get("http://localhost:" + this.port + "/signup");
		assertEquals("http://localhost:" + this.port + "/signup", driver.getCurrentUrl());

		driver.get("http://localhost:" + this.port + "/home");
		assertNotEquals("http://localhost:" + this.port + "/home", driver.getCurrentUrl());
	}

	@Test
	public void userCantSeeHomePageAfterLogout(){
		doMockSignUp("teste","teste","teste2","teste");
		doLogIn("teste2","teste");
		driver.get("http://localhost:" + this.port + "/home");
		assertEquals("Home", driver.getTitle());

		WebDriverWait webDriverWait = new WebDriverWait(driver, 2);
		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("logout-button")));
		WebElement logoutButton = driver.findElement(By.id("logout-button"));
		logoutButton.click();

		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("login-button")));
		driver.get("http://localhost:" + this.port + "/home");
		assertNotEquals("Home", driver.getTitle());
	}


	/**
	 * PLEASE DO NOT DELETE THIS TEST. You may modify this test to work with the 
	 * rest of your code. 
	 * This test is provided by Udacity to perform some basic sanity testing of 
	 * your code to ensure that it meets certain rubric criteria. 
	 * 
	 * If this test is failing, please ensure that you are handling bad URLs 
	 * gracefully, for example with a custom error page.
	 * 
	 * Read more about custom error pages at: 
	 * https://attacomsian.com/blog/spring-boot-custom-error-page#displaying-custom-error-page
	 */
	@Test
	public void testBadUrl() {
		// Create a test account
		doMockSignUp("URL","Test","UT","123");
		doLogIn("UT", "123");
		
		// Try to access a random made-up URL.
		driver.get("http://localhost:" + this.port + "/some-random-page");
		Assertions.assertFalse(driver.getPageSource().contains("Whitelabel Error Page"));
	}


	/**
	 * PLEASE DO NOT DELETE THIS TEST. You may modify this test to work with the 
	 * rest of your code. 
	 * This test is provided by Udacity to perform some basic sanity testing of 
	 * your code to ensure that it meets certain rubric criteria. 
	 * 
	 * If this test is failing, please ensure that you are handling uploading large files (>1MB),
	 * gracefully in your code. 
	 * 
	 * Read more about file size limits here: 
	 * https://spring.io/guides/gs/uploading-files/ under the "Tuning File Upload Limits" section.
	 */
	@Test
	public void testLargeUpload() {
		// Create a test account
		doMockSignUp("Large File","Test","LFT","123");
		doLogIn("LFT", "123");

		// Try to upload an arbitrary large file
		WebDriverWait webDriverWait = new WebDriverWait(driver, 2);
		String fileName = "upload5m.zip";

		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("fileUpload")));
		WebElement fileSelectButton = driver.findElement(By.id("fileUpload"));
		fileSelectButton.sendKeys(new File(fileName).getAbsolutePath());

		WebElement uploadButton = driver.findElement(By.id("uploadButton"));
		uploadButton.click();
		try {
			webDriverWait.until(ExpectedConditions.presenceOfElementLocated(By.id("success")));
		} catch (org.openqa.selenium.TimeoutException e) {
			System.out.println("Large File upload failed");
		}
		Assertions.assertFalse(driver.getPageSource().contains("HTTP Status 403 – Forbidden"));
	}

	@Test
	public void testUploadFile(){
		doMockSignUp("Normal File","Test","NFL","123");
		doLogIn("NFL", "123");

		// Try to upload an arbitrary large file
		WebDriverWait webDriverWait = new WebDriverWait(driver, 2);
		String fileName = "README.md";

		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("fileUpload")));
		WebElement fileSelectButton = driver.findElement(By.id("fileUpload"));
		fileSelectButton.sendKeys(new File(fileName).getAbsolutePath());

		WebElement uploadButton = driver.findElement(By.id("uploadButton"));
		uploadButton.click();


		try {
			webDriverWait.until(ExpectedConditions.presenceOfElementLocated(By.id("success")));
		} catch (org.openqa.selenium.TimeoutException e) {
			System.out.println("upload failed");
		}
		Assertions.assertTrue(driver.getPageSource().contains("File uploaded successfully!"));
	}

	@Test
	public void testDeleteFile() {
		doMockSignUp("delete File","Test","DFL","123");
		doLogIn("DFL", "123");

		// Try to upload an arbitrary large file
		WebDriverWait webDriverWait = new WebDriverWait(driver, 2);
		String fileName = "README.md";

		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("fileUpload")));
		WebElement fileSelectButton = driver.findElement(By.id("fileUpload"));
		fileSelectButton.sendKeys(new File(fileName).getAbsolutePath());

		WebElement uploadButton = driver.findElement(By.id("uploadButton"));
		uploadButton.click();


		try {
			webDriverWait.until(ExpectedConditions.presenceOfElementLocated(By.id("success")));
		} catch (org.openqa.selenium.TimeoutException e) {
			System.out.println("upload failed");
		}

		//Delete the file
		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.name("redirect-link")));
		WebElement redirectLink = driver.findElement(By.name("redirect-link"));
		redirectLink.click();

		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("deleteButton")));
		WebElement deleteButton = driver.findElement(By.id("deleteButton"));
		deleteButton.click();

		Assertions.assertTrue(driver.getPageSource().contains("File deleted successfully!"));

	}

	public void doSaveNoteMockUp(String firstName, String lastName, String username, String password){
		doMockSignUp(firstName,lastName,username,password);
		doLogIn(username, password);
		WebDriverWait webDriverWait = new WebDriverWait(driver, 2);
		WebElement navNotesButton = driver.findElement(By.id("nav-notes-tab"));
		navNotesButton.click();

		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("add-note-button")));
		WebElement addNoteButton = driver.findElement(By.id("add-note-button"));
		addNoteButton.click();

		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("note-title")));
		WebElement noteTitle = driver.findElement(By.id("note-title"));
		noteTitle.sendKeys("Title of the note");
		WebElement noteDesc = driver.findElement(By.id("note-description"));
		noteDesc.sendKeys("Description of the note test");

		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("noteSubmitFooter")));
		WebElement saveNoteButton = driver.findElement(By.id("noteSubmitFooter"));
		saveNoteButton.click();

		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.name("redirect-link")));
		WebElement redirectLink = driver.findElement(By.name("redirect-link"));
		redirectLink.click();

		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("add-note-button")));

		Assertions.assertTrue(driver.getPageSource().contains("Title of the note"));
		Assertions.assertTrue(driver.getPageSource().contains("Description of the note test"));
	}

	@Test
	public void testEditNote(){
		doSaveNoteMockUp("edit note","Test","editnote","123");
		WebDriverWait webDriverWait = new WebDriverWait(driver, 2);

		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("add-note-button")));
		WebElement editButton = driver.findElement(By.id("edit-note-button"));
		editButton.click();

		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("note-title")));
		WebElement noteTitle = driver.findElement(By.id("note-title"));
		noteTitle.clear();
		noteTitle.sendKeys("Title edited");
		WebElement noteDesc = driver.findElement(By.id("note-description"));
		noteDesc.clear();
		noteDesc.sendKeys("Description edited");

		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("noteSubmitFooter")));
		WebElement saveNoteButton = driver.findElement(By.id("noteSubmitFooter"));
		saveNoteButton.click();

		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.name("redirect-link")));
		WebElement redirectLink = driver.findElement(By.name("redirect-link"));
		redirectLink.click();

		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("add-note-button")));

		Assertions.assertTrue(driver.getPageSource().contains("Title edited"));
		Assertions.assertTrue(driver.getPageSource().contains("Description edited"));

	}

	@Test
	public void testDeleteNote(){

		doSaveNoteMockUp("delete note","Test","deletenote","123");
		WebDriverWait webDriverWait = new WebDriverWait(driver, 2);

		WebElement deleteButton = driver.findElement(By.id("delete-note-button"));
		deleteButton.click();

		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.name("redirect-link")));
		WebElement redirectLink = driver.findElement(By.name("redirect-link"));
		redirectLink.click();

		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("add-note-button")));

		Assertions.assertFalse(driver.getPageSource().contains("Title edited"));
		Assertions.assertFalse(driver.getPageSource().contains("Description edited"));
	}

	public void doSaveCredentialMockUp(String firstName, String lastName, String username, String password){
		doMockSignUp(firstName,lastName,username,password);
		doLogIn(username,password);
		WebDriverWait webDriverWait = new WebDriverWait(driver, 2);
		WebElement navNotesButton = driver.findElement(By.id("nav-credentials-tab"));
		navNotesButton.click();

		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("add-credential-button")));
		WebElement addCredentialButton = driver.findElement(By.id("add-credential-button"));
		addCredentialButton.click();

		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("credential-url")));
		WebElement credentialUrl = driver.findElement(By.id("credential-url"));
		credentialUrl.sendKeys("https://test.com");
		WebElement credentialUsername = driver.findElement(By.id("credential-username"));
		credentialUsername.sendKeys("usertest");
		WebElement credentialPassword = driver.findElement(By.id("credential-password"));
		credentialPassword.sendKeys("testpassword");

		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("credentialSubmitFooter")));
		WebElement saveCredentialButton = driver.findElement(By.id("credentialSubmitFooter"));
		saveCredentialButton.click();

		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.name("redirect-link")));
		WebElement redirectLink = driver.findElement(By.name("redirect-link"));
		redirectLink.click();

		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("add-credential-button")));

		Assertions.assertTrue(driver.getPageSource().contains("https://test.com"));
		Assertions.assertTrue(driver.getPageSource().contains("usertest"));
		Assertions.assertFalse(driver.getPageSource().contains("testpassword"));
	}

	@Test
	public void TestEditCredential(){
		doSaveCredentialMockUp("Edit credential", "teste", "ECR", "1234");
		WebDriverWait webDriverWait = new WebDriverWait(driver, 2);

		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("edit-credential-button")));
		WebElement editButton = driver.findElement(By.id("edit-credential-button"));
		editButton.click();

		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("credential-url")));
		WebElement credentialUrl = driver.findElement(By.id("credential-url"));
		credentialUrl.clear();
		credentialUrl.sendKeys("https://google.com");

		WebElement credentialUsername = driver.findElement(By.id("credential-username"));
		credentialUsername.clear();
		credentialUsername.sendKeys("other_username");

		WebElement credentialPassword = driver.findElement(By.id("credential-password"));
		credentialPassword.clear();
		credentialPassword.sendKeys("a better password");

		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("credentialSubmitFooter")));
		WebElement saveNoteButton = driver.findElement(By.id("credentialSubmitFooter"));
		saveNoteButton.click();

		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.name("redirect-link")));
		WebElement redirectLink = driver.findElement(By.name("redirect-link"));
		redirectLink.click();

		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("add-credential-button")));

		Assertions.assertTrue(driver.getPageSource().contains("https://google.com"));
		Assertions.assertTrue(driver.getPageSource().contains("other_username"));
		Assertions.assertFalse(driver.getPageSource().contains("a better password"));
	}

	@Test
	public void testDeleteCredentials(){
		doSaveCredentialMockUp("Delete Credential", "test", "deletecred", "1234");
		WebDriverWait webDriverWait = new WebDriverWait(driver, 2);

		WebElement deleteButton = driver.findElement(By.id("delete-credential-button"));
		deleteButton.click();

		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.name("redirect-link")));
		WebElement redirectLink = driver.findElement(By.name("redirect-link"));
		redirectLink.click();

		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("add-credential-button")));

		Assertions.assertFalse(driver.getPageSource().contains("https://test.com"));
		Assertions.assertFalse(driver.getPageSource().contains("usertest"));
		Assertions.assertFalse(driver.getPageSource().contains("testpassword"));

	}

}
