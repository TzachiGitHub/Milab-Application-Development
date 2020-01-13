const admin = require("firebase-admin"),
bodyparser = require('body-parser'), 
express = require('express'),
webApp = express(),

//initiallizing alpha-vantage properties by my specific key
AlphaVantageAPI = require('alpha-vantage-cli').AlphaVantageAPI;
var alphaApiKey = '0KDLRM8UFFZ3CQZQ',
alphaVantageAPI = new AlphaVantageAPI(alphaApiKey, 'compact', true),
stockPrice,
stockName
tagCount = 1;

//initiallizing firebase properties
var serviceAccount = require("./stocksfirebasepushnotification-firebase-adminsdk-fu5pf-91a0158ca5.json");
admin.initializeApp({
  credential: admin.credential.cert(serviceAccount),
});

// Configuring Notification Properties
var firebaseToken,
payload,
options = {
  priority: "high",
  timeToLive: 60 * 60 * 24
};

// activates first when the token is being sent from the app to the server
webApp.post("/:token", (req, res)=> {
  firebaseToken = req.params.token;
  res.send({"status": "good job!"});
  console.log("got the token, before the stock was chosen");
})


webApp.post("/:stock/:token", (req, res)=>{
  // This registration token comes from the client FCM SDKs (Android Studio App)
  firebaseToken = req.params.token;
  stockName = (req.params.stock).toUpperCase();
  console.log("firebaseToken = " + firebaseToken + "\n stockName = " + stockName);
  //handler for the alpha-vantage api and firebase messages.  
  alphaVantageAPI.getDailyData(stockName)
  .then(dailyData => {
    stockPrice = dailyData[0].Close;
    
    payload = {
      data: {
        MyKey1: (stockName + "'s current stock price is " + stockPrice)
      },
      // changing the tag parameter will let the notification be re-written at the user's device.
      notification: {
        "title" : stockName + "'s Stock is currently worth:",
        "body" : "" + stockPrice,
        "tag" : "Notification Tag"
      }
    };
    
      admin.messaging().sendToDevice(firebaseToken, payload, options)
      .then((response) => {
          //Response is a message ID string.
          // console.log("Successfully send message", response);
      })
      .catch ((error)=> {
          console.log("Error sending message: ", error);
      })
  
      var seconds = 15;
      var the_interval = seconds * 1000;
      setInterval(()=>{
        console.log("I am doing my 15 seconds check!");
        //call for the handler.
        activate();
      }, the_interval);
      res.send({stockPrice: stockPrice});
  
  })
  .catch(err => {
        console.log("Couldn't get the stockPrice, and the err is - " + err);
        res.send({stockPrice: null});
  });
});

var activate = ()=> {
  // This registration token comes from the client FCM SDKs (Android Studio App)
  //sending the query for alphaVantage to re-check the price and send it to the user.
  alphaVantageAPI.getDailyData(stockName)
  .then(dailyData => {
    stockPrice = dailyData[0].Close;
    payload.data.MyKey1 = (stockName + "'s current stock price is " + stockPrice)
    })
    .catch(err => {
      console.error(err);
  });
  //changing the tag for the notification so it will be re-written to show the current stock.
  // payload.notification.body += (" " + tagCount++);

  admin.messaging().sendToDevice(firebaseToken, payload, options)
    .then((response) => {
        //Response is a message ID string.
        console.log("Successfully send message", stockPrice);
    })
    .catch ((error)=> {
        console.log("Error sending message: ", error);
    })
}

//listener
webApp.listen(3000, ()=>{
    console.log("Started listening on port 3000");
})