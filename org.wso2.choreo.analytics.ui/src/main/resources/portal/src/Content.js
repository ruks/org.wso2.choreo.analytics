import React, { Component } from 'react';
import { makeStyles } from '@material-ui/core/styles';
import InputLabel from '@material-ui/core/InputLabel';
import MenuItem from '@material-ui/core/MenuItem';
import FormHelperText from '@material-ui/core/FormHelperText';
import FormControl from '@material-ui/core/FormControl';
import Select from '@material-ui/core/Select';
import { withStyles } from '@material-ui/core/styles';
import Grid from '@material-ui/core/Grid';
import BarChart from './barChart';
import { ApolloClient, InMemoryCache, createHttpLink } from '@apollo/client';
import { gql } from '@apollo/client';
import Button from '@material-ui/core/Button';
import { setContext } from '@apollo/client/link/context';


const styles = (theme) => ({
    formControl: {
        margin: theme.spacing(1),
        minWidth: 120,
    },
    selectEmpty: {
        marginTop: theme.spacing(2),
    },
});

class Content extends Component {
    constructor() {
        super();
        this.state = {
            envionments: ['QA', 'Test', 'Production', 'INT'],
            env: '',
            data: [],
        };
        this.envHandleChange = this.envHandleChange.bind(this);
        this.refreshClick = this.refreshClick.bind(this);
        const httpLink = createHttpLink({
            uri: 'http://localhost:8080/analytics-restapi/query',
            credentials: 'include'
        });
        const envLink = setContext((_, { headers }) => {
            return {
                headers: {
                    ...headers,
                    Environment: 'dev',
                }
            }
        });
        this.client = new ApolloClient({
            cache: new InMemoryCache(),
            link: envLink.concat(httpLink),
        });
    }

    envHandleChange(e) {
        console.log(e);
        this.setState({ 'env': e.target.value });
    }

    componentDidMount() {
        this.refreshClick();
    }

    refreshClick() {
        console.log('refreshClick');
        this.client
            .query({
                query: gql`
                {
                    apiLatencySummary(from: "2020-10-10T14:43:16.023+05:30", to: "2020-11-06T14:43:16.023+05:30", limit: 10, orderBy: "apiName", asc: true) {
                      id
                      apiName
                      apiVersion
                      apiResourceTemplate
                      apiMethod
                      avgResponseLatency
                      avgServiceLatency
                      avgBackendLatency
                      avgRequestMediationLatency
                      avgResponseMediationLatency
                      avgSecurityLatency
                      avgThrottlingLatency
                      avgOtherLatency
                    }
                  }

            `,
                fetchPolicy: 'no-cache'
            })
            .then(result => {
                window.result = result;
                console.log(result.data.apiLatencySummary);
                this.setState({ data: result.data.apiLatencySummary });
            });
    }

    render() {
        const { classes } = this.props;
        const { envionments, env, data } = this.state;

        return (
            <div>
                <Grid container spacing={5}>
                    <Grid item xs={9}>
                    </Grid>
                    <Grid item xs={3}>
                        <FormControl className={classes.formControl}>
                            <InputLabel id="demo-simple-select-label">Environment</InputLabel>
                            <Select
                                labelId="demo-simple-select-label"
                                id="demo-simple-select"
                                value={env}
                                onChange={this.envHandleChange}
                            >
                                {envionments.map((env) =>
                                    <MenuItem value={env}>{env}</MenuItem>
                                )}
                            </Select>
                        </FormControl>
                    </Grid>
                    <Grid item xs={1}>
                    </Grid>
                    <Grid item xs={2}>
                        <Button variant="outlined" color="primary" onClick={this.refreshClick}>
                            Refresh
                        </Button>
                    </Grid>
                    <Grid item xs={12}>
                        <BarChart data={data} />
                    </Grid>
                </Grid>

            </div>
        );
    }
}
export default withStyles(styles)(Content);
