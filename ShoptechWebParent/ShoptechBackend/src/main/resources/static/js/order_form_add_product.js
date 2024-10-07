let productDetailCount;

$(document).ready(function(){
    productDetailCount = $(".hiddenProductId").length;

    $("#products").on("click", "#linkAddProduct", function(e){
       e.preventDefault();
       $("#addProductModal").modal();
       let link = $(this);
       let url = link.attr("href");

       $("#addProductModal").on("shown.bs.modal", function(){
          $(this).find("iframe").attr("src", url);
       });
    });
});

function getShippingCost(productId){
    let selectedCountry = $("#country option:selected");
    let countryId = selectedCountry.val();
    let state = $("#state").val();
    if(state.length == 0){
        state = $("#city").val();
    }

    let requestURL = contextPath + "get_shipping_cost";
    let params = {productId: productId, countryId: countryId, state: state};
    $.ajax({
        type: 'POST',
        url: requestURL,
        beforeSend: function(xhr) {
            xhr.setRequestHeader(csrfHeaderName, csrfValue);
        },
        data: params
    }).done(function(shippingCost) {
        getProductInfo(productId, shippingCost);
    }).fail(function(err) {
        showWarningModal(err.responseJSON.message);
        shippingCost = 0.0;
        getProductInfo(productId, shippingCost);
    }).always(function(){
        $("#addProductModal").modal("hide");
    });
}

function getProductInfo(productId, shippingCost){
    let requestURL = contextPath + "products/get/" + productId;
    $.get(requestURL, function (productJson){
        let productName = productJson.name;
        let mainImagePath = contextPath.substring(0, contextPath.length - 1) + productJson.imagePath;
        let productCost = $.number(productJson.cost, 2);
        let productPrice = $.number(productJson.price, 2);

       let htmlCode = generateProductCode(productId, productName, mainImagePath, productCost, productPrice, shippingCost);
        $("#productList").append(htmlCode);

        updateOrderAmounts();

    }).fail(function(err){
        showWarningModal(err.responseJSON.message);
    });
}

function addProduct(productId, productName){
    $("#addProductModal").modal('hide');
    getShippingCost(productId);
}

function generateProductCode(productId, productName, mainImagePath, productCost, productPrice, shippingCost){
    let nextCount = productDetailCount + 1;
    productDetailCount++;
    let quantityId = "quantity" + nextCount;
    let priceId = "price" + nextCount;
    let subtotalId = "subtotal" + nextCount;
    let rowId = "row" + nextCount;
    let blankLineId = "blankLine" + nextCount;

    let htmlCode = `<div class="border rounded p-1" id="${rowId}">
                    <input type="hidden" name="detailId" value="0">
                    <input type="hidden" name="productId" value="${productId}" class="hiddenProductId">
                    <div class="row">
                        <div class="col-1">
                            <div class="divCount">${nextCount}</div>
                            <div><a href="" class="fas fa-trash icon-dark linkRemove" rowNumber="${nextCount}"></a></div>
                        </div>
                        <div class="col-3">
                            <img src="${mainImagePath}" width="200" />
                        </div>
                    </div>
                    <div class="row m-2">
                        <b>${productName}</b>
                    </div>
                    <div class="row m-2">
                        <table>
                            <tr>
                                <td>Product Cost</td>
                                <td>
                                    <input type="text" value="${productCost}"
                                            name="productDetailCost"
                                            rowNumber="${nextCount}"
                                            class="form-control m-1 cost-input" style="max-width: 140px;"
                                            required>
                                </td>
                            </tr>
                            <tr>
                                <td>Quantity</td>
                                <td>
                                    <input type="number" value="1"
                                            name="quantity"
                                            rowNumber="${nextCount}"
                                            id="${quantityId}"
                                            step="1" min="1" max="5"
                                            class="form-control m-1 quantity-input" style="max-width: 140px;"
                                            required>
                                </td>
                            </tr>
                            <tr>
                                <td>Unit Price</td>
                                <td>
                                    <input type="text" value="${productPrice}"
                                            name="productPrice"
                                            id="${priceId}"
                                            rowNumber="${nextCount}"
                                            class="form-control m-1 price-input" style="max-width: 140px;"
                                            required>
                                </td>
                            </tr>
                            <tr>
                                <td>Subtotal</td>
                                <td>
                                    <input type="text" value="${productPrice}"
                                            name="productSubtotal"
                                            id="${subtotalId}"
                                            class="form-control m-1 subtotal-output" 
                                            style="max-width: 140px;"
                                            readonly required>
                                </td>
                            </tr>
                            <tr>
                                <td>Shipping Cost</td>
                                <td>
                                    <input type="text" value="${shippingCost}"
                                            name="productShipCost"
                                            class="form-control m-1 ship-input" style="max-width: 140px;"
                                            required>
                                </td>
                            </tr>
                        </table>
                    </div>
                </div>
                <div id="${blankLineId}" class="row">&nbsp;</div>`;
    return htmlCode;
}

function isProductAlreadyAdded(productId){
    let productExists = false;

    $(".hiddenProductId").each(function(e){
       let aProductId = $(this).val();
       if(aProductId == productId){
           productExists = true;
           return;
       }
    });

    return productExists;
}