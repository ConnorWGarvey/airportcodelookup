package com

import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeDriverService
import org.openqa.selenium.firefox.FirefoxDriver
import org.openqa.selenium.remote.DesiredCapabilities
import org.openqa.selenium.remote.RemoteWebDriver
import org.openqa.selenium.support.ui.ExpectedCondition
import org.openqa.selenium.support.ui.WebDriverWait

class CodeSearch {
    private static final AIRPORT_LABELS = ['Airport', 'Field', 'Airstrip']
    private WebDriver driver
    private static ChromeDriverService service
    
    public static void main(args) {
      startService()
      def search = new CodeSearch()
      search.createDriver()
      for (code in ['PEA', 'PEB', 'PED', 'PEE', 'PEF', 'PEG', 'PEH', 'PEI', 'PEJ', 'PEK', 'PEL', 'PEM', 'PEN', 'PEP', 'PEQ', 'PER', 'PES', 'PET', 'PEU', 'PEV', 'PEW', 'PEX', 'PEY', 'PEZ', 'PFA', 'PFB', 'PFC', 'PFD', 'PFJ', 'PFN', 'PFO', 'PFQ', 'PFR']) {
        search.doSearch(code:code)
      }
      search.stopDriver()
      stopService()
    }
    
    private static startService() {
        if (new File('/opt/google/chromedriver/chromedriver').exists()) {
            service = new ChromeDriverService.Builder()
                    .usingChromeDriverExecutable(new File('/opt/google/chromedriver/chromedriver'))
                    .usingAnyFreePort()
                    .build()
            service.start()
        }
    }
    
    private static void stopService() {
        if (service) service.stop()
    }
    
    private void createDriver() {
        if (service) driver = new RemoteWebDriver(service.getUrl(), DesiredCapabilities.chrome())
        else driver = new FirefoxDriver()
    }
    
    private void stopDriver() {
        driver.quit()
    }
    
    private void doSearch(Map options=[:]) {
        driver.get('http://www.world-airport-codes.com/')
        setText('criteria', options.code)
        driver.findElement(By.xpath('//*[@id="maincontent"]/div[3]/div[1]/div/center/form/input[3]')).click()
        def airport = driver.findElement(By.xpath('//*[@id="maincontent"]/div[2]/div/div[1]/span[2]')).text
        airport = airport.substring(1, airport.length() - 3).trim()
        def city = driver.findElement(By.xpath('//*[@id="maincontent"]/div[2]/div/div[1]/span[5]')).text
        city = city.substring(1, city.length() - 3).trim()
        println "${city}\t\t${englishAirport(airport)}"
    }
    
    private String englishAirport(String airport) {
      if (AIRPORT_LABELS.any { airport.contains(it) }) {
        return airport
      }
      else {
        return airport + ' Airport'
      }
    }
    
    private String spanishAirport(String airport) {
      for (label in AIRPORT_LABELS) {
        airport = (airport =~ ' ' + label).replaceAll('')
      }
      'Aeropuerto de ' + airport
    }
    
    private void setText(String field, String text) {
        driver.findElement(By.name(field)).sendKeys(text)
    }
    
    void waitForElement(Map options) {
        if (options.xpath) waitUntil {it.findElement(By.xpath(options.xpath)) != null}
        else if (options.name) waitUntil {it.findElement(By.name(options.name)) != null}
    }
    
    void waitUntil(Closure closure, int timeout=10) {
        (new WebDriverWait(driver, 10)).until(new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver d) {
                return closure(d)
            }
        });
    }
}

