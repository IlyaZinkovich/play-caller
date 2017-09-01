$("#phone").intlTelInput();

$("#search").click(function() {
  var countryData = $("#phone").intlTelInput("getSelectedCountryData");
  var countryCode = countryData.iso2.toUpperCase()
  var phoneNumber = $("#phone").intlTelInput("getNumber", intlTelInputUtils.numberFormat.E164);
  console.log(phoneNumber.replace("+" + countryData.dialCode, ""));

  $.get("http://localhost:9000/search?countryCode=" + countryCode + "&phoneNumber=" + phoneNumber, function( data ) {
    var name = data.name;
    $("#name").html(name);

    if (data.phones != null) {
      var phone = data.phones[0];
      var phoneNumber = phone.nationalFormat;
      $("#phoneNumber").html(phoneNumber);
      var carrier = phone.carrier;
      $("#carrier").html(carrier);
      var spamScore = phone.spamScore;
      $("#spamScore").html(spamScore);
      var spamType = phone.spamType;
      $("#spamType").html(spamType);
    }
  });
})