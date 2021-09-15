<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <title>WebSocket - Top5 Applicaition</title>
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
    <script src="https://code.jquery.com/jquery-3.3.1.min.js"></script>
    <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/sockjs-client/1.3.0/sockjs.min.js"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/stomp.js/2.3.3/stomp.min.js"></script>
    <script type="text/javascript">
        var stompClient = null;

        function setConnected(connected) {
            $("#connect").prop("disabled", connected);
            $("#disconnect").prop("disabled", !connected);
            if (connected) {
                $("#conversation").show();
            }
            else {
                $("#conversation").hide();
            }
            $("#conversation").html("");
        }

        function connect(context) {

            var socket = new SockJS('http://45.77.14.84:9898/dashboard-ws');
            stompClient = Stomp.over(socket);
            stompClient.connect({}, function (frame) {
                setConnected(true);
                console.log('Connected: ' + frame);
                stompClient.subscribe('/dashboard/switch/stat', function (data) {
                    var contentFromSockjs = JSON.parse(data.body).content;
                    $("#conversation").html(data.body);
                });
            });
        }

        function disconnect() {
            if (stompClient != null) {
                stompClient.disconnect();
            }
            setConnected(false);
            console.log("Disconnected");
        }


        $(function () {

            $("form").on('submit', function (e) {
                e.preventDefault();
            });
            $( "#connect" ).click(function() { connect(this); });
            $( "#disconnect" ).click(function() { disconnect(); });

        });
    </script>
</head>
<body>
<div id="main-content" class="container">
    <div class="row">
        <div class="col-md-6">
            <form class="form-inline">
                <div class="form-group">
                    <label for="connect">WebSocket connection:</label>
                    <button id="connect" class="btn btn-default" type="submit">Connect</button>
                    <button id="disconnect" class="btn btn-default" type="submit" disabled="disabled">Disconnect
                    </button>
                </div>
            </form>
        </div>
    </div>
    <div class="row">
        <div class="col-md-12">
            <div id="conversation" class="table table-striped">
                <br/>
            </div>
        </div>
    </div>
</div>
</body>
</html>