package Intern::Bookmark::Config;

use strict;
use warnings;
use utf8;

use Intern::Bookmark::Config::Route;

use Config::ENV 'INTERN_BOOKMARK_ENV', export => 'config';
use Path::Class qw(file);

my $Router = Intern::Bookmark::Config::Route->make_router;
my $Root = file(__FILE__)->dir->parent->parent->parent->absolute;

sub router { $Router }
sub root { $Root }

common {
};

my $port = $ENV{PORT} || 3000;
my $origin = $ENV{ORIGIN} || "http://localhost:${port}";
my $database_host = $ENV{DATABASE_HOST} || 'localhost';
my $database_user = $ENV{DATABASE_USER} || 'root';
my $database_password = $ENV{DATABASE_PASSWORD} || '';
my $database_name = $ENV{DATABASE_NAME} || 'intern_bookmark';
my $hatena_oauth_consumer_key = $ENV{HATENA_OAUTH_CONSUMER_KEY} || 'xxxxx';
my $hatena_oauth_consumer_secret = $ENV{HATENA_OAUTH_CONSUMER_SECRET} || 'xxxxx';

config default => {
    'server.port'     => $port,
    'origin'          => $origin,
    'file.log.access' => 'log/access_log',
    'file.log.error'  => 'log/error_log',
    'dir.static.root'    => 'static',
    'dir.static.favicon' => 'static/images',

    'hatena_oauth.consumer_key'    => $hatena_oauth_consumer_key,
    'hatena_oauth.consumer_secret' => $hatena_oauth_consumer_secret,
    db => {
        intern_bookmark => {
            user     => $database_user,
            password => $database_password,
            dsn      => "dbi:mysql:dbname=$database_name;host=$database_host",
        },
    },
    db_timezone => 'UTC',
};

1;
