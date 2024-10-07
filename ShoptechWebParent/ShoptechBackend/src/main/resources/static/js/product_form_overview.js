dropdownBrands = $("#brand");
dropdownCategories = $("#category");

$(document).ready(function(){
    $("#shortDescription").richText();
    $("#fullDescription").richText();

    dropdownBrands.change(function(){
        dropdownCategories.empty();
        getCategories();
    });
    getCategoriesForNewForm();
});

function getCategoriesForNewForm(){
    catIdField = $('#categoryId');

    editMode = !!catIdField.length;

    if(!editMode) getCategories();
}

function getCategories(){
    brandId = dropdownBrands.val();
    url = brandModuleURL + "/" + brandId + "/categories";

    $.get(url, function(response){
        $.each(response, function(index, category){
            $('<option>').val(category.id).text(category.name).appendTo(dropdownCategories);
        });
    });
}

function checkUnique(form){
    let productId = $("#id").val();
    let productName = $("#name").val();
    let csrf = $("input[name='_csrf']").val();
    let params = {id: productId, name: productName, _csrf: csrf}

    $.post(checkUniqueUrl, params, function(response){
        if(response == "OK"){
            form.submit();
        }else if(response == "DuplicatedName") {
            showWarningModal("There is another product having same name " + productName);
        }else {
            showErrorModal("Unknown response from server");
        }
    }).fail(function(response){
        showErrorModal("Could not connect to the server")
    });
    return false;
}