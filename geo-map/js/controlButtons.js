/**
 * Created by thilini on 12/10/15.
 */

//load control buttons
L.easyButton({
    position: 'topright',
    states: [{
        icon: '<div id="MoveLeftButtonId">&larr;</div>',
        onClick: function () {
            moveToLeft();
        }
    }]
}).addTo(baseMap);
