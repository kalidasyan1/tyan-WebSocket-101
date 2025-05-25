
# 📈 Real-Time Stock Ticker (Java + WebSocket)

This is a simple real-time stock ticker web application that displays **live prices** for the top 7 tech giants using:

- 🧩 **Spring Boot** (Java)
- 🌐 **WebSockets** for real-time updates
- 💻 **HTML + JavaScript** frontend

Prices are simulated every 3 seconds using random fluctuations.


## 🚀 Features

- Real-time updates via WebSocket
- Displays prices for:
    - Apple (AAPL)
    - Microsoft (MSFT)
    - Google (GOOGL)
    - Amazon (AMZN)
    - Meta (META)
    - Tesla (TSLA)
    - Nvidia (NVDA)
- Lightweight single-page frontend
- No external API keys required


## 🏗️ Project Structure
```
stock-ticker/
├── src/
│   ├── main/
│   │   ├── java/com/example/stockticker/
│   │   │   ├── StockTickerApplication.java
│   │   │   ├── StockWebSocketConfig.java
│   │   │   └── StockWebSocketHandler.java
│   │   └── resources/
│   │       └── static/
│   │           └── index.html
├── pom.xml
└── README.md

```

## 📦 Requirements

- Java 17+ (or Java 8 with Spring Boot 2.x)
- Maven 3.6+
- A modern browser


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


# 📡 Real-Time Updates

The backend sends updated stock prices to the frontend via WebSocket every 3 seconds.

If you want to use live stock prices, you can:
	•	Replace StockWebSocketHandler with an API client
	•	Use APIs like Yahoo Finance, Alpha Vantage, or Finnhub


# 🎨 Screenshot
<img src="https://github.com/user-attachments/assets/ac33456c-abec-44b8-b9fe-f33ef9dc140d" width="600" alt="Stock Ticker Preview"/>


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
