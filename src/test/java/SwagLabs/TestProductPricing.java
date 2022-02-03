package SwagLabs;

import java.io.File;
import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import io.github.bonigarcia.wdm.WebDriverManager;

public class TestProductPricing {


	WebDriver driver;
	static String maxPrice;

	
	@BeforeClass
	public void loginToSauceDemo() {

		try
		{
			String URL="https://www.saucedemo.com/";

			WebDriverManager.chromedriver().setup();
			driver=new ChromeDriver();
			driver.manage().window().maximize();
			driver.manage().timeouts().implicitlyWait(Duration.ofMinutes(60L)).pageLoadTimeout(Duration.ofMinutes(60L));
			driver.get(URL);

			String userName=driver.findElement(By.cssSelector("div#login_credentials")).getText().split(":")[1];
			userName=userName.substring(0, (userName.indexOf("_user", 0))+"_user".length());
			String password=driver.findElement(By.cssSelector("div.login_password")).getText().split(":")[1];

			driver.findElement(By.cssSelector("input#user-name")).sendKeys(userName);
			driver.findElement(By.cssSelector("input#password")).sendKeys(password);
			driver.findElement(By.cssSelector("input#login-button")).click();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	
	@Test
	public void addProductOfHighestPriceToCart() throws InterruptedException {
		
		try
		{
			List<WebElement> productPricesWebElement=driver.findElements(By.cssSelector("div.inventory_item_price")); 

			
			List<Double> productPrices=productPricesWebElement.stream().map(x->Double.parseDouble(x.getText().replaceAll("\\$",""))).collect(Collectors.toList());
			maxPrice=String.valueOf(Collections.max(productPrices));


			Function<List<WebElement>, Integer> f= TestProductPricing::getIndexOfWebElement;
			int indexLocation=f.apply(productPricesWebElement);

			driver.findElement(By.xpath("/descendant::div[@class='inventory_item_price']["+indexLocation+"]/following-sibling::button")).click();
			driver.findElement(By.cssSelector("a.shopping_cart_link")).click();
			
			File srcFile=((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
			String destFileName=System.getProperty("user.dir")+System.getProperty("file.separator")+"screenshots"+System.getProperty("file.separator")+"cartPage"+Math.random()+".png";
			FileUtils.copyFile(srcFile, 
					new File(destFileName));
			
			System.out.println("Screenshot generated @ "+destFileName+", Pls check for product addition onto cart page");
			
		    driver.close();
			
		
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		

	}
	
	public static int getIndexOfWebElement(List<WebElement> ele)
	{
		System.out.println(ele);
		int indexLocation=0;
		for(int i=0;i<ele.size();i++)
		{
			if(ele.get(i).getText().replaceAll("\\$","").equals(maxPrice))
			{
				indexLocation=i+1;
				break;
			}
		}
		return indexLocation;
	}

	

}
