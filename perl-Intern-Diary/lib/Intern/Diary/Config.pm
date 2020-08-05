package Intern::Diary::Config;

use strict;
use warnings;
use utf8;

use Intern::Diary::Config::Route;

use Config::ENV 'INTERN_DIARY_ENV', export => 'config';
use Path::Class qw(file);

my $Router = Intern::Diary::Config::Route->make_router;
my $Root = file(__FILE__)->dir->parent->parent->parent->absolute;

sub router { $Router }
sub root { $Root }

common {
};

my $port              = $ENV{PORT}              || 3000;
my $origin            = $ENV{ORIGIN}            || "http://localhost:${port}";
my $database_host     = $ENV{DATABASE_HOST}     || 'localhost';
my $database_user     = $ENV{DATABASE_USER}     || 'root';
my $database_password = $ENV{DATABASE_PASSWORD} || '';
my $database_name     = $ENV{DATABASE_NAME}     || 'intern_diary';

config default => {
    'server.port'        => $port,
    'origin'             => $origin,
    'file.log.access'    => 'log/access_log',
    'file.log.error'     => 'log/error_log',
    'dir.static.root'    => 'static',
    'dir.static.favicon' => 'static/images',

    db => {
        intern_diary => {
            user     => $database_user,
            password => $database_password,
            dsn      => "dbi:mysql:dbname=${database_name};host=${database_host}",
        },
    },
    db_timezone => 'UTC',
};

1;
