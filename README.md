
# 📈 Real-Time Stock Ticker (Java + WebSocket)

This project is a real-time stock ticker web app built with **Spring Boot**, **Java WebSocket**, and **HTML/JavaScript**, designed to display prices for major tech stocks using **live data from Finnhub**, with **automatic fallback to Alpha Vantage** when needed.
## 🛠️ Tech Stack
- 🧩 **Spring Boot** (Java)
- 🌐 **WebSockets** for real-time updates
- 💻 **HTML + JavaScript** frontend

## 🚀 Features

✅ Real-time price updates via **Finnhub WebSocket**  
🔁 Fallback to **Alpha Vantage API** if:
- Market is closed (no trade data)
- WebSocket connection is stale
- API/WebSocket errors occur

📡 Stock Symbols Tracked:
- Apple (AAPL)
- Microsoft (MSFT)
- Google (GOOGL)
- Amazon (AMZN)
- Meta (META)
- Tesla (TSLA)
- Nvidia (NVDA)

🖥️ Frontend:
- HTML + WebSocket JS client
- Live price updates directly in the browser

## 🧱 Architecture Overview
```
[Finnhub WebSocket]         ← primary data source
│
▼
(Java ClientEndpoint)
│
▼
[Spring WebSocket Handler] ──> Browser WebSocket clients
▲
[Fallback: Alpha Vantage API] ← when Finnhub is stale or down
```


## 🏗️ Project Structure
```
stock-ticker/
├── src/
│   ├── main/
│   │   ├── java/com/example/stockticker/
│   │   │   ├── StockTickerApplication.java
│   │   │   ├── StockWebSocketConfig.java
│   │   │   ├── StockWebSocketHandler.java
│   │   │   ├── FinnhubWebSocketClient.java
│   │   │   └── AlphaVantageClient.java
│   │   └── resources/
│   │       └── static/
│   │           └── index.html
├── pom.xml
└── README.md

```

## 🚀 Setup

### 1. Prerequisites

- Java 17+ (or Java 11 with compatible Spring Boot version)
- Maven 3.6+
- Internet connection (for API access)
- Free API keys:
    - [✅ Finnhub](https://finnhub.io)
    - [✅ Alpha Vantage](https://www.alphavantage.co/support/#api-key)

### 2. Configure API Keys

Edit your `AlphaVantageClient.java` and `FinnhubWebSocketClient.java` and replace:

```java
private static final String API_KEY = "YOUR_ALPHA_VANTAGE_KEY";
String uri = "wss://ws.finnhub.io?token=YOUR_FINNHUB_KEY";
```


## 🛠️ Getting Started

### 1. Clone the project

```bash
git clone https://github.com/your-username/stock-ticker.git
cd stock-ticker
```

### 2. Build the project

mvn clean install

### 3. Run the server

mvn spring-boot:run

The app will be available at:
👉 http://localhost:8080


# 🧠 How Fallback Works
The server checks:
```java
if (finnhub.isStale(19_000)) {
  // use Alpha Vantage
} else {
  // use Finnhub's latestPrices cache
}
```
- If no trade data received in 30 seconds, fallback triggers
- Alpha Vantage is polled in parallel for all stocks
- All data is merged into a JSON payload sent to the browser


# 🎨 Screenshot
<img src="https://github.com/user-attachments/assets/ac33456c-abec-44b8-b9fe-f33ef9dc140d" width="600" alt="Stock Ticker Preview"/>

# 🔐 Rate Limits
* API  Limit (free tier)
* Finnhub:  60 WebSocket messages/min
* Alpha Vantage: 5 requests/min (1 symbol/call)

To avoid hitting these:
- Do not increase polling frequency below 15 seconds
- Avoid too many browser clients


# 📜 License

MIT License.
Feel free to modify or extend the project.


# 🙋 FAQ

Q: Does this fetch real prices?
A: No — this simulates real-time updates. You can easily integrate live APIs.

Q: Can I deploy this on a server?
A: Yes — you can build a .jar and run it on any Java-enabled host.


# ‍💻 Author

Created by Tao Yan — pull requests welcome!
