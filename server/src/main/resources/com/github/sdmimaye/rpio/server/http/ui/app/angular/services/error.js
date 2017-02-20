angular.module('rpio').service('error', function(lang, message){
    return {
        show: function (path, unknown, response) {
            if(response.status === 483){//application error
                var parts = path.split(".");
                var start = lang.getLang();
                parts.forEach(function(p){
                    if(start){start = start[p];}
                });

                var errors = response.data;
                var result = errors.map(function(e){
                    var trans = start ? start[e.message] : "[" + path + "." + e.message + "]";
                    if(trans) return trans;

                    return "[" + path + "." + e.message + "]";
                }).join("\r\n");

                message.error(result);
            }else{
                var uparts = unknown.split(".");
                var ustart = lang.getLang();
                uparts.forEach(function(p){
                    if(ustart){ustart = ustart[p];}
                });

                message.error(ustart ? ustart : "[" + unknown + "]");
            }
        }
    };
});