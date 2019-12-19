const express = require('express'),
bodyParser = require('body-parser'),
jsonfile = require('jsonfile'),
querystring = require('querystring'),
app = express(),
fs = require('fs');


app.use(bodyParser.urlencoded({ extended: false}));
app.use(bodyParser.json());


app.get('/tasks', (req, res) => {
	//fetching the file todoList.JSON from the current folder
	var rawFile = fs.readFileSync('todoList.JSON');
	
	//parsing the file to a JSON format so it can be accessed convinietly
	var jsonFile = JSON.parse(rawFile);
	res.send(jsonFile.tasks);
});

app.post("/tasks/new", (req, res) =>{
	let id = parseInt(req.query.id);
	let task = req.query.task;
	
	//fetching the file and parsing it in a string format
	var rawFile = fs.readFileSync('todoList.JSON');
	var jsonFile = JSON.parse(rawFile);
	
	// pushing the new entry to the last of the to-do list
	jsonFile.tasks.push({id: id, name: task});

	// changing the jsonFile back to a JSON file - readable one
	jsonFile = JSON.stringify(jsonFile, null, 2);
	
	// writing to the file - re-writing on it using the new file with the new entry
	fs.writeFile('todoList.JSON', jsonFile, (err)=>{
		if(err){
			console.log(err);
		}
	});
	//once worked - send the user to ther index page - show all tasks
	res.redirect("/tasks");
});

app.delete("/tasks/remove", (req, res)=>{
	//fetching the file and parsing it in a str	ing format
	var rawFile = fs.readFileSync('todoList.JSON');
	var jsonFile = JSON.parse(rawFile);
	let id = parseInt(req.query.id);

	var index = 0;

	// checks to see where is the item needed to be deleted
	jsonFile.tasks.forEach((task)=>{
		//if the input id == task.id we got a match
		if (task["id"].valueOf() == id.valueOf()){
			// deletes the said item from the jsonFile.tasks
			jsonFile.tasks.splice(index, 1);	
		} else { 
			//raises the index of the item we're looking for
			index++;
		}
	});
	
			
	// changing the jsonFile back to a JSON file - readable one
	jsonFile = JSON.stringify(jsonFile, null, 3);
	// writing to the file - re-writing on it using the new file with the new entry
	fs.writeFile('todoList.JSON', jsonFile, (err)=>{
		if(err){
			console.log(err);
		}
	});

	res.redirect("/tasks");	
});


// listener on port 3000
app.listen(3000, () => {
	console.log("example app listening on port 3000!");
});

