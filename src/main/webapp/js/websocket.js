/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/* global socketUrl, userId, WebSocket, userName, userUrl, userImg */

var actions = {
    JOIN_CHAT: "join",
    LEAVE_CHAT: "leave",
    CHAT_MSG: "chat",
    UPDATE_LIST: "update"
};
var by = {
    id: "Id",
    tag: "TagName",
    class: "ClassName"
};

function start() {
    websocket = new WebSocket(socketUrl);
    websocket.onmessage = onMessage;
    websocket.onopen = onOpen;
    websocket.onclose = onClose;
    websocket.onerror = onError;
    setTimeout(function () {
        joinChat();
    }, 1000);
}

function onOpen(event) {/*Session created.*/
    updateChatBox("Server connected...");
}

function onClose(event) {/*Session closed - e.g Server down/unavailable*/
    updateChatBox("Server disconnected...");
}

function onError(event) {/*Error occured while communicating server...*/
}

function onMessage(event) {
    var response = JSON.parse(event.data);
    if (response.action === actions.JOIN_CHAT) {
        updateUserList(response);
        if (response.message !== undefined)
            updateChatBox(response.name + " " + response.message);
    }
    //If new user left chat room, notify others and update users list
    if (response.action === actions.LEAVE_CHAT) {
        if (parseInt(response.id) === userId)
            return;
        var onlineList = getElement("onlineList", by.id);
        onlineList.removeChild(getElement(response.id, by.id));

        updateChatBox(response.name + " " + response.message);
    }

    if (response.action === actions.UPDATE_LIST) {
        if (parseInt(response.id) === userId)
            return;
        updateUserList(response);
        updateChatBox(response.name + " " + response.message);
    }

    // Send message
    if (response.action === actions.CHAT_MSG) {
        var senderName = response.name;
        updateChatBox(response.name + ": " + response.message);
    }
}
function updateChatBox(message) {
    getElement("textAreaMessage", by.id).innerHTML += message + " \n";
}
/*
 function processMessage(message) {
 console.log(message);
 textAreaMessage.value += message.data + " \n";
 }
 */
function sendMessage() {
    var message = getElementText("textMessage", by.id);
    message = message.trim();
    if (message === null || message === "") {
        return;
    }
    var request = {
        action: actions.CHAT_MSG,
        id: userId,
        message: message
    };
    setElementText("textMessage", by.id, "");
    getElement("textMessage", by.id).focus();
    sendRequest(request);
}

function joinChat() {
    var request = {
        action: actions.JOIN_CHAT,
        id: userId,
        name: userName,
        url: userUrl,
        img: userImg
    };
    sendRequest(request);
}

function leaveChat() {
    var request = {
        action: actions.LEAVE_CHAT,
        id: userId
    };
    sendRequest(request);
}

function sendRequest(request) {
    if (websocket === undefined || websocket.readyState === WebSocket.CLOSED) {
        alert("Connection lost to Server. Please try again later.");
        return;
    }
    websocket.send(JSON.stringify(request));
}

function closeSocket() {
    leaveChat();
    setTimeout(function () {
        if (websocket !== undefined || websocket.readyState !== WebSocket.CLOSED) {
            websocket.close();
        }
        return;
    }, 1000);

}

// Stuffs
function getElement(id, type) {
    var element;
    switch (type) {
        case by.id:
            element = document.getElementById(id);
            break;
        case by.tag:
            element = document.getElementsByTagName(id);
            break;
        default:
            element = document.getElementById(id);
            break;
    }
    return element;
}

function setElementText(id, by, text) {
    getElement(id, by).value = text;
}

function getElementText(id, by) {
    return getElement(id, by).value;
}

function updateUserList(user) {
    // To-do: more functions here
    var onlineList = getElement("onlineList", by.id);
    var u = document.createElement("a");
    u.setAttribute("id", user.id);
    u.setAttribute("href", user.url);

    var u_img = document.createElement("img");
    u_img.src = user.img;
    u.appendChild(u_img);
    onlineList.appendChild(u);
}

// Execute a function when the user releases a key on the keyboard
var input = document.getElementById("textMessage");
input.addEventListener("keyup", function (event) {
    // Cancel the default action, if needed
    event.preventDefault();
    // Number 13 is the "Enter" key on the keyboard
    if (event.keyCode === 13) {
        // Trigger the button element with a click
        document.getElementById("sendMsg").click();
    }
});