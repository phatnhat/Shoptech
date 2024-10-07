let returnModal;
let modalTitle;
let fieldNote;
let orderId;
let divReason;
let divMessage;
let firstButton;
let secondButton;

$(document).ready(function(){
    returnModal = $("#returnOrderModal");
    modalTitle = $("#returnOrderModalTitle");
    fieldNote = $("#returnNote");
    divReason = $("#divReason");
    divMessage = $("#divMessage");
    firstButton = $("#firstButton");
    secondButton = $("#secondButton");

   handleReturnOrderLink($(this));
});

function showReturnModalDialog(link){
    divMessage.hide();
    divReason.show();
    firstButton.show();
    secondButton.text("Cancel");
    fieldNote.val("");

    orderId = link.attr("orderId");
    returnModal.modal('show');
    modalTitle.text("Return Order ID #" + orderId);
}

function showMessageDialog(message){
    divReason.hide();
    firstButton.hide();
    secondButton.text("Close");
    divMessage.text(message);

    divMessage.show();
}

function handleReturnOrderLink(){
    $(".linkReturnOrder").on("click", function(e){
        e.preventDefault();
        showReturnModalDialog($(this));
    });
}

function submitReturnOrderForm(){
    let reason = $("input[name='returnReason']:checked").val();
    let note = fieldNote.val();

    sendReturnOrderRequest(reason, note);

    return false;
}

function sendReturnOrderRequest(reason, note){
    let requestURL = contextPath + "orders/return";
    let requestBody = {orderId: orderId, reason: reason, note: note};

    $.ajax({
        type: "POST",
        url: requestURL,
        beforeSend: function(xhr){
            xhr.setRequestHeader(csrfHeaderName, csrfValue);
        },
        data: JSON.stringify(requestBody),
        contentType: 'application/json'
    }).done(function(requestResponse){
        showMessageDialog("Return request has been sent");
        updateStatusTextAndHideReturnButton(orderId);
    }).fail(function(err){
        showMessageDialog(err.responseText);
    });
}

function updateStatusTextAndHideReturnButton(orderId){
    $(".textOrderStatus" + orderId).each(function(index){
       $(this).text("RETURN_REQUESTED");
    });
    $(".linkReturn" + orderId).each(function(index){
       $(this).hide();
    });
}
