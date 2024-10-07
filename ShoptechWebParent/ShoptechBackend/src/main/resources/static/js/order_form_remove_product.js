$(document).ready(function(){
   $("#productList").on('click', ".linkRemove", function(e){
      e.preventDefault();

      if(doesOderHaveOnlyOneProduct()){
          showWarningModal("Could not remove product. The product must have at least one product.");
      }else{
          removeProduct($(this));
          updateOrderAmounts();
      }
   });
});

function removeProduct(input){
    let rowNumber = input.attr("rowNumber");
    $('#row' + rowNumber).remove();
    $("#blankLine" + rowNumber).remove();

    $(".divCount").each(function(idx, element){
       element.innerHTML = (idx + 1).toString() ;
    });
}

function doesOderHaveOnlyOneProduct(){
    let productCount = $(".hiddenProductId").length;
    return productCount == 1;
}