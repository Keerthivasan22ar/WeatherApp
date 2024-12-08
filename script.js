// Handle Current Weather Form Submission
document.getElementById("weatherForm").addEventListener("submit", function (event) {
  event.preventDefault(); // Prevent the default form submission behavior

  const city = document.getElementById("cityInput").value; // Get city input
  const resultDiv = document.getElementById("weatherResult"); // Target result div

  // Call the backend API for current weather
  fetch(`http://localhost:8080/api/weather?city=${city}`)
      .then((response) => response.json())
      .then((data) => {
          if (data.error) {
              // Display error message for invalid city
              resultDiv.innerHTML = `<p style="color: red;">${data.error}</p>`;
          } else {
              // Display current weather details
              resultDiv.innerHTML = `
                  <h2>Current Weather in ${data.name}</h2>
                  <p>Temperature: ${(data.main.temp - 273.15).toFixed(2)}°C</p>
                  <p>Humidity: ${data.main.humidity}%</p>
                  <p>Condition: ${data.weather[0].main}</p>
                  <p>Wind Speed: ${data.wind.speed} m/s</p>
              `;
          }
      })
      .catch((error) => {
          // Handle fetch or server errors
          resultDiv.innerHTML = `<p style="color: red;">Failed to fetch weather data. Please try again later.</p>`;
      });
});

// Handle Weather Forecast Form Submission
document.getElementById("forecastForm").addEventListener("submit", function (event) {
  event.preventDefault(); // Prevent the default form submission behavior

  const city = document.getElementById("forecastCityInput").value; // Get city input
  const days = document.getElementById("forecastDaysInput").value; // Get number of days input
  const resultDiv = document.getElementById("forecastResult"); // Target result div

  // Validate number of days
  if (days < 1 || days > 7) {
      resultDiv.innerHTML = `<p style="color: red;">Please provide a valid number of days (1–7).</p>`;
      return;
  }

  // Call the backend API for weather forecast
  fetch(`http://localhost:8080/api/forecast?city=${city}&days=${days}`)
      .then((response) => response.json())
      .then((data) => {
          if (data.error) {
              // Display error message for invalid city
              resultDiv.innerHTML = `<p style="color: red;">${data.error}</p>`;
          } else {
              // Display forecast details
              let forecastHTML = `<h2>Weather Forecast for ${city} (${days} days)</h2>`;
              data.forEach((day) => {
                  let date;
                  // If the date is in Unix timestamp format (seconds)
                  if (typeof day.date === "number") {
                      date = new Date(day.date * 1000).toLocaleDateString(); // Convert Unix timestamp to date
                  } else {
                      date = day.date; // If it's already in YYYY-MM-DD format
                  }
                  forecastHTML += `
                      <div>
                          <h3>${date}</h3>
                          <p>Average Temperature: ${day.avgTemp.toFixed(2)}°C</p>
                          <p>Max Temperature: ${day.maxTemp.toFixed(2)}°C</p>
                          <p>Min Temperature: ${day.minTemp.toFixed(2)}°C</p>
                          <p>Condition: ${day.condition}</p>
                      </div>
                      <hr>
                  `;
              });
              resultDiv.innerHTML = forecastHTML;
          }
      })
      .catch((error) => {
          // Handle fetch or server errors
          resultDiv.innerHTML = `<p style="color: red;">Failed to fetch forecast data. Please try again later.</p>`;
      });
});
