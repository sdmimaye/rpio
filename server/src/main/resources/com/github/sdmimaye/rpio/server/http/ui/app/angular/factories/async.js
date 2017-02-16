angular.module('rpio').service('async', function (atmosphereService) {
    function parseAsyncMessage(message){
        var body = message.responseBody;
        var index = body.lastIndexOf("|");
        if (index <= 0)
            return null;

        var size = parseInt(body.substr(0, index));
        if(size === 0)
            return null;

        var content = body.substr(index + 1, size);
        return {size: size, data: angular.fromJson(content)};
    }

    return {
        listen: function (url, onMessage, onConnectionEstablished) {
            var request = {
                url: url,
                contentType: 'application/json',
                transport: 'websocket',
                reconnectInterval: 5000,
                enableXDR: true,
                timeout: 60000,
                logLevel: 'error',
                closeAsync: true
            };

            request.onMessage = function (response) {
                var message = parseAsyncMessage(response);
                if (message === null)
                    return;

                if (onMessage !== undefined)
                    onMessage(message);
            };

            request.onOpen = function () {
                console.log("WebSocket connection established: " + url);
                if (onConnectionEstablished !== undefined)
                    onConnectionEstablished();
            };

            return atmosphereService.subscribe(request);
        }
    };
});