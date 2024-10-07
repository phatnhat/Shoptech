$(document).ready(function(){
    $(".linkMinus").click(function(e){
        e.preventDefault();

        let productId = $(this).attr("pid");
        let quantityInput = $('#quantity' + productId);
        let newQuantity = parseInt(quantityInput.val()) - 1;

        if(newQuantity > 0){
            quantityInput.val(newQuantity);
        }else{
            showWarningModal("Minimum quantity is 1");
        }
    });

    $(".linkPlus").click(function(e){
        e.preventDefault();

        let productId = $(this).attr("pid");
        let quantityInput = $('#quantity' + productId);
        let newQuantity = parseInt(quantityInput.val()) + 1;

        if(newQuantity <= 5){
            quantityInput.val(newQuantity);
        }else{
            showWarningModal("Maximum quantity is 5");
        }
    });
})