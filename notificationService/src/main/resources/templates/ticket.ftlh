<#ftl encoding="UTF-8">
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <title>Reservation Confirmation – ${eventName}</title>
    <style>
        #body {
            display: flex;
            flex-direction: column;
            font-family: "Segoe UI", serif;
            align-items: center;
            background-color: #607FF2;
            font-size: 20px;
            height: 100%;
        }

        #div {
            display: block;
            flex-direction: column;
            margin: 10%;
            font-family: "Segoe UI", serif;
            background-color: white;
            padding: 3%;
            border-radius: 20px;
            color: black;
        }

        #a {
            text-decoration: none;
        }
    </style>
</head>
<body>
<div id="body">
    <div id="div">
        <h1 style="text-align: center">TicRes</h1>

        <p>Hello, ${nameOfClient}!</p>
        <p>Your ticket reservation has been successfully processed.</p>

        <h3>Reservation details:</h3>
        <ul>
            <li><strong>Event:</strong> ${eventName}</li>
            <li><strong>Ticket type:</strong> ${ticketType}</li>
            <li><strong>Place:</strong> ${location}</li>
            <li><strong>Date:</strong> ${date}</li>
            <li><strong>Price:</strong> ${ticketPrice}</li>
        </ul>

        <p>You will receive additional information if needed.</p>

        <p>With best regards, <br> TicRes Team</p>

        <a id="a" href="https://github.com/snjk333/TicRes">Github</a>
    </div>
</div>
</body>
</html>
