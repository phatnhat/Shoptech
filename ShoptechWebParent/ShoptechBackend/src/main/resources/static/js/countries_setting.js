let buttonLoad;
let dropDownCountry;
let buttonAddCountry;
let buttonUpdateCountry;
let buttonDeleteCountry;
let labelCountryName;
let fieldCountryName;
let fieldCountryCode;

$(document).ready(function() {
    buttonLoad = $("#buttonLoadCountries");
    dropDownCountry = $("#dropDownCountries");
    buttonAddCountry = $("#buttonAddCountry");
    buttonUpdateCountry = $("#buttonUpdateCountry");
    buttonDeleteCountry = $("#buttonDeleteCountry");
    labelCountryName = $("#labelCountryName");
    fieldCountryName = $("#fieldCountryName");
    fieldCountryCode = $("#fieldCountryCode");

    fieldCountryName.prop("disabled", true);
    fieldCountryCode.prop("disabled", true);

    buttonLoad.click(function() {
        loadCountries();
    });

    dropDownCountry.on("change", function() {
        changeFormStateToSelectedCountry();
    });

    buttonAddCountry.click(function() {
        if ($(this).val() === "Add") {
            if(checkUniqueCountry()){
                addCountry();
            }
        } else {
            changeFormStateToNewCountry();
        }
    });

    buttonUpdateCountry.click(function() {
        if(checkUniqueCountry()){
            updateCountry();
        }
    });

    buttonDeleteCountry.click(function() {
        deleteCountry();
    });
});

function deleteCountry() {
    optionValue = dropDownCountry.val();
    countryId = optionValue.split("-")[0];

    url = contextPath + "countries/delete/" + countryId;

    $.ajax({
        type: 'DELETE',
        url: url,
        beforeSend: function(xhr) {
            xhr.setRequestHeader(csrfHeaderName, csrfValue);
        }
    }).done(function() {
        $("#dropDownCountries option[value='" + optionValue + "']").remove();
        changeFormStateToNewCountry();
        showToastMessage("The country has been deleted");
    }).fail(function() {
        showToastMessage("ERROR: Could not connect to server or server encountered an error");
    });
}

function updateCountry() {
    if (!validateFormCountry()) return;

    url = contextPath + "countries/save";
    countryName = fieldCountryName.val();
    countryCode = fieldCountryCode.val();

    countryId = dropDownCountry.val().split("-")[0];

    jsonData = {id: countryId, name: countryName, code: countryCode};

    $.ajax({
        type: 'POST',
        url: url,
        beforeSend: function(xhr) {
            xhr.setRequestHeader(csrfHeaderName, csrfValue);
        },
        data: JSON.stringify(jsonData),
        contentType: 'application/json'
    }).done(function(countryId) {
        $("#dropDownCountries option:selected").val(countryId + "-" + countryCode);
        $("#dropDownCountries option:selected").text(countryName);
        showToastMessage("The country has been updated");

        changeFormStateToNewCountry();
    }).fail(function() {
        showToastMessage("ERROR: Could not connect to server or server encountered an error");
    });
}

function addCountry() {
    if (!validateFormCountry()) {
        return;
    }

    url = contextPath + "countries/save";
    countryName = fieldCountryName.val();
    countryCode = fieldCountryCode.val();
    jsonData = {name: countryName, code: countryCode};

    $.ajax({
        type: 'POST',
        url: url,
        beforeSend: function(xhr) {
            xhr.setRequestHeader(csrfHeaderName, csrfValue);
        },
        data: JSON.stringify(jsonData),
        contentType: 'application/json'
    }).done(function(countryId) {
        selectNewlyAddedCountry(countryId, countryCode, countryName);
        showToastMessage("The new country has been added");
    }).fail(function() {
        showToastMessage("ERROR: Could not connect to server or server encountered an error");
    });

}

function selectNewlyAddedCountry(countryId, countryCode, countryName) {
    optionValue = countryId + "-" + countryCode;
    $("<option>").val(optionValue).text(countryName).appendTo(dropDownCountry);

    //$("#dropDownCountries option[value='" + optionValue + "']").prop("selected", true);

    fieldCountryCode.val("");
    fieldCountryName.val("").focus();
}

function changeFormStateToNewCountry() {
    buttonAddCountry.val("Add");
    labelCountryName.text("Country Name:");

    fieldCountryName.prop("disabled", false);
    fieldCountryCode.prop("disabled", false);

    buttonUpdateCountry.prop("disabled", false);
    buttonDeleteCountry.prop("disabled", false);

    fieldCountryCode.val("");
    fieldCountryName.val("").focus();
}

function changeFormStateToSelectedCountry() {
    buttonAddCountry.prop("value", "New");

    fieldCountryName.prop("disabled", false);
    fieldCountryCode.prop("disabled", false);

    buttonUpdateCountry.prop("disabled", false);
    buttonDeleteCountry.prop("disabled", false);

    labelCountryName.text("Selected Country:");

    selectedCountryName = $("#dropDownCountries option:selected").text();
    fieldCountryName.val(selectedCountryName);

    countryCode = dropDownCountry.val().split("-")[1];
    fieldCountryCode.val(countryCode);

}

function loadCountries() {
    url = contextPath + "countries/list";
    $.get(url, function(responseJSON) {
        dropDownCountry.empty();

        $.each(responseJSON, function(index, country) {
            optionValue = country.id + "-" + country.code;
            $("<option>").val(optionValue).text(country.name).appendTo(dropDownCountry);
        });

    }).done(function() {
        buttonLoad.val("Refresh Country List");
        showToastMessage("All countries have been loaded");
    }).fail(function() {
        showToastMessage("ERROR: Could not connect to server or server encountered an error");
    });
}

function showToastMessage(message) {
    $("#toastMessage").text(message);
    $(".toast").toast('show');
}

function validateFormCountry() {
    formCountry = document.getElementById("formCountry");
    if (!formCountry.checkValidity()) {
        formCountry.reportValidity();
        return false;
    }

    return true;
}


function checkUniqueCountry() {

    console.log("checkUnique is working");

    countryId = 0;
    if(dropDownCountry.val()){
        countryId = dropDownCountry.val().split("-")[0];
    }

    countryName = $("#fieldCountryName").val();

    console.log(countryName);

    csrfValue = $("input[name='_csrf']").val();

    jsonData = {id: countryId, name: countryName, _csrf: csrfValue};

    checkUniqueUrl = contextPath + "countries/check_unique";

    let flag_check = false;

    $.ajax({
        method: 'POST',
        url: checkUniqueUrl,
        beforeSend: function(xhr) {
            xhr.setRequestHeader(csrfHeaderName, csrfValue);
        },
        data: JSON.stringify(jsonData),
        contentType: 'application/json',
        async: false,
        success: function(response){
            if (response === "OK") {
                showToastMessage("OK");
                flag_check = true;
            } else if (response === "Duplicate") {
                showToastMessage("Country already added :  " + countryName);
                flag_check = false;
            } else {
                showToastMessage("Unknown response from server");
                flag_check =  false;
            }
        },
        error: function(){
            showToastMessage("ERROR: Could not connect to server or server encountered an error");
            flag_check = false;
        }
    });

    return flag_check;
}