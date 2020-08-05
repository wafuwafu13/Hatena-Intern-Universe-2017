package Intern::Bookmark::Context;

use strict;
use warnings;
use utf8;

use Carp ();
use Encode ();

use HTTP::Status;
use HTTP::Throwable::Factory ();

use URI;
use URI::QueryParam;

use DBIx::Sunny;

use Plack::Session;

use Class::Accessor::Lite::Lazy (
    rw_lazy => [ qw(request response stash) ],
    rw      => [ qw(env) ],
    new     => 1,
);

use Intern::Bookmark::Request;
use Intern::Bookmark::Config;
use Intern::Bookmark::Service::User;

### Properties

sub from_env {
    my ($class, $env) = @_;
    return $class->new(env => $env);
}

sub _build_request {
    my $self = shift;

    return undef unless $self->env;
    return Intern::Bookmark::Request->new($self->env);
};

sub _build_response {
    my $self = shift;
    return $self->request->new_response(200);
};

sub _build_stash { +{} };

*req = \&request;
*res = \&response;

### HTTP Response

sub render_file {
    my ($self, $file, $args) = @_;
    $args //= {};

    require Intern::Bookmark::View::Xslate;
    my $content = Intern::Bookmark::View::Xslate->render_file($file, {
        c => $self,
        %{ $self->stash },
        %$args
    });
    return $content;
}

sub html {
    my ($self, $file, $args) = @_;

    my $content = $self->render_file($file, $args);
    $self->response->code(200);
    $self->response->content_type('text/html; charset=utf-8');
    $self->response->content(Encode::encode_utf8 $content);
}

sub json {
    my ($self, $hash) = @_;

    require JSON::XS;
    $self->response->code(200);
    $self->response->content_type('application/json; charset=utf-8');
    $self->response->content(JSON::XS::encode_json($hash));
}

sub plain_text {
    my ($self, @lines) = @_;
    $self->response->code(200);
    $self->response->content_type('text/plain; charset=utf-8');
    $self->response->content(join "\n", @lines);
}

sub throw {
    my ($self, $code, $message, %opts) = @_;
    HTTP::Throwable::Factory->throw({
        status_code => $code,
        reason      => HTTP::Status::status_message($code),
        message     => $message // '',
        %opts,
    });
}

sub throw_redirect {
    my ($self, $url) = @_;
    HTTP::Throwable::Factory->throw(Found => { location => $url });
}

sub uri_for {
    my ($self, $path_query) = @_;
    my $uri = URI->new(config->param('origin'));
    $uri->path_query($path_query);
    return $uri;
}

### DB Access

sub db_config {
    my ($self, $name) = @_;
    my $db_config = config->param('db')
        // Carp::croak 'A DB setting is required';
    return $db_config->{$name}
        // Carp::croak qq(No DB config for DB '$name' exists);
}

sub dbh {
    my ($self) = @_;

    my $name = 'intern_bookmark';
    my $db_config = $self->db_config($name);
    my $user = $db_config->{user}
        or Carp::croak qq(No user name for DB '$name' exists);
    my $dsn = $db_config->{dsn}
        or Carp::croak qq(No dsn for DB '$name' exists);
    my $password = $db_config->{password};

    return DBIx::Sunny->connect($dsn, $user, $password);
}

### Session

sub session {
    my ($self) = @_;
    return Plack::Session->new($self->env);
}

sub user {
    my ($self) = @_;
    return $self->{user} ||= do {
        my $user_info = $self->session->get('hatenaoauth_user_info') || {};
        my $user_name = $user_info->{url_name} or return '';
        Intern::Bookmark::Service::User->find_or_create_user_by_name($self->dbh, {
            name => $user_name
        });
    };
}

1;
