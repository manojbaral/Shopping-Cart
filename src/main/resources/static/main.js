/**
 * Created by Manoj Baral on 9/25/2017.
 */

$( document ).ready(function() {

    $('.item-qty').on('input', function() {

        var id = this.id.substring(4);

        $('#update-'+id).css("display", "inline-block");



    });
//
//     $('.subtotal').each(function(){
//         total=total + parseFloat($(this).text());
//     });
//
//     $('#total').text(total);
//     $('#hidden-total').val(total);
});
