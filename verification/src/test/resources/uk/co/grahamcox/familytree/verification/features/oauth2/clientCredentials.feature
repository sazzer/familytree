Feature: Client Credentials Grant

    @wip
    Scenario: Authenticating with an unknown client
        #Given that no client exists with an id of "unknown"
        When I perform a client credentials authentication for:
            | Client | unknown |
            | Secret | Secret  |
        Then I get an authentication error of "invalid_client"

    @wip
    Scenario: Authenticating with an incorrect secret
        #Given that a user exists:
        #And that a client exists:
        When I perform a client credentials authentication for:
            | Client | graham |
            | Secret | Secret |
        Then I get an authentication error of "invalid_client"

    @wip
    Scenario: Authenticating with a correct client and secret
        #Given that a user exists:
        #And that a client exists:
        When I perform a client credentials authentication for:
            | Client | graham |
            | Secret | 1234   |
        Then I successfully authenticated

    @wip
    Scenario: Checking that I authenticated successfully
        #Given that a user exists:
        #And that a client exists:
        When I perform a client credentials authentication for client "graham" with secret "1234"
        And I look up who I'm logged in as
        Then I am authenticated
        And my authenticated credentials are:
        | Client | graham  |
        | User   | graham  |
        | Scopes | a, b, c |
        And my authenticated principal is:
        | Username    | graham                 |
        | Authorities | ROLE_a, ROLE_b, ROLE_c |

#{
#  "credentials": {
#    "accessTokenId": {
#      "id": "49545933-0b8e-45f2-beed-8ca2f644a9fd"
#    },
#    "client": {
#      "id": "graham"
#    },
#    "user": {
#      "id": "graham"
#    },
#    "issued": "2016-01-19T20:04:55Z",
#    "expires": "2016-01-19T21:04:55Z",
#    "scopes": {
#      "scopes": [
#        "b",
#        "c"
#      ]
#    }
#  },
#  "principal": {
#    "password": null,
#    "username": "graham",
#    "authorities": [
#      {
#        "authority": "ROLE_b"
#      },
#      {
#        "authority": "ROLE_c"
#      }
#    ],
#    "accountNonExpired": true,
#    "accountNonLocked": true,
#    "credentialsNonExpired": true,
#    "enabled": true
#  },
#  "details": {
#    "remoteAddress": "127.0.0.1",
#    "sessionId": null
#  },
#  "authorities": [
#    {
#      "authority": "ROLE_b"
#    },
#    {
#      "authority": "ROLE_c"
#    }
#  ],
#  "authenticated": true,
#  "name": "graham"
#}
