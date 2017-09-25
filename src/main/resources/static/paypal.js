/**
 * Created by Manoj Baral on 9/25/2017.
 */
paypal.Button.render({

    env: 'sandbox', // Optional: specify 'sandbox' environment

    client: {
        sandbox:    'AfHTJqsen816NYDTl09yY_tw10bzoKFxRzOi3czoGLPn5N4I2jdKRyIcg0PiUOqa7KjfuMC-vKQL1p86',
        production: 'xxxxxxxxx'
    },

    payment: function() {

        var env    = this.props.env;
        var client = this.props.client;
        var total = document.getElementById("total").textContent;
        console.log("Total amount is: "+total);

        return paypal.rest.payment.create(env, client, {
            transactions: [
                {
                    amount: { total: total, currency: 'USD' }
                }
            ]
        });
    },

    commit: true, // Optional: show a 'Pay Now' button in the checkout flow

    onAuthorize: function(data, actions) {

        // Optional: display a confirmation page here

        return actions.payment.execute().then(function() {
            // Show a success page to the buyer
            document.getElementById("payment-success").style.display="inline";
        });
    }

}, '#paypal-button');
