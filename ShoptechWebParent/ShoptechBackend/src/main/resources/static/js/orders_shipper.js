let iconNames = {
    'PICKED': "fa-people-carry",
    'SHIPPING': "fa-shipping-fast",
    'DELIVERED': "fa-box-open",
    'RETURNED': "fa-undo"
}
let confirmText;
let confirmModalDialog;
let yesButton;
let noButton;

$(document).ready(function(){
    confirmText = $("#confirmText");
    confirmModalDialog = $("#confirmDialog");
    yesButton = $("#yesButton");
    noButton = $("#noButton");

    $(".linkUpdateStatus").on("click", function(e){
        e.preventDefault();
        let link = $(this);
        showUpdateConfirmModal(link);
    });

    addEventHandlerForYesButton();
});

function addEventHandlerForYesButton(){
    yesButton.click(function(e){
        e.preventDefault();
        sendRequestToUpdateOrderStatus($(this));
    });
}

function sendRequestToUpdateOrderStatus(button){
    let requestURL = button.attr('href');

    $.ajax({
        type: 'POST',
        url: requestURL,
        beforeSend: function(xhr) {
            xhr.setRequestHeader(csrfHeaderName, csrfValue);
        },
    }).done(function(response) {
        showMessageModel("Order updated successfully.");
        updateStatusIconColor(response.orderId, response.status);
    }).fail(function(err) {
        showMessageModel("Error updating order status.");
    });
}

function updateStatusIconColor(orderId, status){
    let link = $("#link" + status + orderId);
    link.replaceWith(`<i class="fas `+ iconNames[status] + ` fa-2x icon-green"></i>`)
}

function showUpdateConfirmModal(link){
    noButton.text("No");
    yesButton.show();

    let orderId = link.attr("orderId");
    let status = link.attr("status");
    yesButton.attr('href', link.attr('href'));

    confirmText.text("Are you sure you want to update status of the order ID #" +
        orderId + " to " + status + "?");
    confirmModalDialog.modal();
}

function showMessageModel(message){
    noButton.text("Close");
    yesButton.hide();
    confirmText.text(message)
}